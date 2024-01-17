package com.hisun.codeassistant.actions.editor.popupmenu;

import com.hisun.codeassistant.actions.notifications.HiCodeAssistantNotification;
import com.hisun.codeassistant.constant.DefaultConst;
import com.hisun.codeassistant.constant.PromptConst;
import com.hisun.codeassistant.enums.EditorActionEnum;
import com.hisun.codeassistant.enums.SessionTypeEnum;
import com.hisun.codeassistant.toolwindows.HiCodeAssistantChatToolWindowFactory;
import com.hisun.codeassistant.toolwindows.chat.HiCodeAssistantChatToolWindow;
import com.hisun.codeassistant.toolwindows.components.EditorInfo;
import com.hisun.codeassistant.settings.configuration.EditorActionConfigurationState;
import com.hisun.codeassistant.utils.DocumentUtil;
import com.hisun.codeassistant.utils.PerformanceCheckUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Deprecated
public class PopupMenuEditorActionGroupUtil {
    private static final Map<String, Icon> ICONS = new LinkedHashMap<>(Map.of(
            EditorActionEnum.PERFORMANCE_CHECK.getLabel(), AllIcons.Plugins.Updated,
            EditorActionEnum.GENERATE_COMMENTS.getLabel(), AllIcons.Actions.InlayRenameInCommentsActive,
            EditorActionEnum.GENERATE_TESTS.getLabel(), AllIcons.Modules.GeneratedTestRoot,
            EditorActionEnum.FIX_THIS.getLabel(), AllIcons.Actions.QuickfixBulb,
            EditorActionEnum.REVIEW_CODE.getLabel(), AllIcons.Actions.PreviewDetailsVertically,
            EditorActionEnum.EXPLAIN_THIS.getLabel(), AllIcons.Actions.Preview));

    public static void refreshActions() {
        AnAction actionGroup = ActionManager.getInstance().getAction("com.hisun.codeassistant.actions.editor.popupmenu.BasicEditorAction");
        if (actionGroup instanceof DefaultActionGroup) {
            DefaultActionGroup group = (DefaultActionGroup) actionGroup;
            group.removeAll();
            group.add(new NewChatAction());
            group.addSeparator();

            var defaultActions = EditorActionConfigurationState.getInstance().getDefaultActions();
            defaultActions.forEach((label, prompt) -> {
                var action = new BasicEditorAction(label, label, ICONS.getOrDefault(label, AllIcons.FileTypes.Unknown)) {
                    @Override
                    protected void actionPerformed(Project project, Editor editor, String selectedText) {
                        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("HiCodeAssistant");
                        toolWindow.show();
                        if (isInputExceedLimit(selectedText, prompt)) {
                            HiCodeAssistantNotification.info("请减少提供的上下文或清除会话记录");
                            return;
                        }

                        Consumer<String> callback = result -> {
                            if (validateResult(result)) {
                                HiCodeAssistantNotification.info("请减少提供的上下文或清除会话记录");
                                return;
                            }

                            EditorActionEnum editorActionEnum = EditorActionEnum.getEnumByLabel(label);
                            if (Objects.isNull(editorActionEnum)) {
                                return;
                            }
                            switch (editorActionEnum) {
                                case PERFORMANCE_CHECK:
                                    // display result, and open diff window
                                    PerformanceCheckUtils.showDiffWindow(selectedText, project, editor);
                                    break;
                                case GENERATE_COMMENTS:
                                    DocumentUtil.diffCommentAndFormatWindow(project, editor, result);
                                    break;
                                default:
                                    break;
                            }
                        };

                        EditorInfo editorInfo = new EditorInfo(editor);

                        HiCodeAssistantChatToolWindow hiCodeAssistantChatToolWindow = HiCodeAssistantChatToolWindowFactory.getHiCodeAssistantChatToolWindow(project);
                        // right action clear session
                        hiCodeAssistantChatToolWindow.addClearSessionInfo();
                        String newPrompt = prompt.replace("{{selectedCode}}", selectedText);
                        newPrompt += PromptConst.ANSWER_IN_CHINESE;
                        hiCodeAssistantChatToolWindow.syncSendAndDisplay(SessionTypeEnum.MULTI_TURN.getCode(), EditorActionEnum.getEnumByLabel(label), newPrompt,
                                callback, editorInfo);
                    }
                };
                group.add(action);
            });
        }
    }

    public static void registerOrReplaceAction(AnAction action) {
        ActionManager actionManager = ActionManager.getInstance();
        var actionId = action.getTemplateText();
        if (actionManager.getAction(actionId) != null) {
            actionManager.replaceAction(actionId, action);
        } else {
            actionManager.registerAction(actionId, action, PluginId.getId("com.hisun.code-assistant"));
        }
    }

    /**
     * check input length
     *
     * @return
     */
    private static boolean validateResult(String content) {
        return content.contains(DefaultConst.MAX_TOKEN_EXCEPTION_MSG);
    }

    /**
     * check length of input rather than max limit
     * 1 token = 3 english character()
     *
     * @param content
     * @return
     */
    private static boolean isInputExceedLimit(String content, String prompt) {
        // text too long, openai server always timeout
        if (content.length() + prompt.length() > DefaultConst.TOKEN_MAX_LENGTH) {
            return true;
        }
        // valid chinese and english character length
        return DocumentUtil.getChineseCharCount(content + prompt) / 2 + DocumentUtil.getEnglishCharCount(content + prompt) / 4 > DefaultConst.TOKEN_MAX_LENGTH;
    }
}
