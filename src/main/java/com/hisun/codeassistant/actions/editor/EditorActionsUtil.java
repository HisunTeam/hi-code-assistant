package com.hisun.codeassistant.actions.editor;

import com.hisun.codeassistant.HiCodeAssistantKeys;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.embedding.ReferencedFile;
import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowContentManager;
import com.hisun.codeassistant.utils.file.FileUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import org.apache.commons.text.CaseUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class EditorActionsUtil {
    public static Map<String, String> DEFAULT_ACTIONS = new LinkedHashMap<>(Map.of(
            EditorActionEnum.REVIEW.getLabel(), EditorActionEnum.REVIEW.getPrompt(),
            EditorActionEnum.REFACTOR.getLabel(), EditorActionEnum.REFACTOR.getPrompt(),
            EditorActionEnum.PERFORMANCE.getLabel(), EditorActionEnum.PERFORMANCE.getPrompt(),
            EditorActionEnum.SECURITY.getLabel(), EditorActionEnum.SECURITY.getPrompt(),
            EditorActionEnum.OPTIMIZE.getLabel(), EditorActionEnum.OPTIMIZE.getPrompt(),
            EditorActionEnum.COMMENT.getLabel(), EditorActionEnum.COMMENT.getPrompt(),
            EditorActionEnum.EXPLAIN.getLabel(), EditorActionEnum.EXPLAIN.getPrompt(),
            EditorActionEnum.GENERATE_TESTS.getLabel(), EditorActionEnum.GENERATE_TESTS.getPrompt()));

    public static void refreshActions() {
        AnAction actionGroup =
                ActionManager.getInstance().getAction("HiCodeAssistantEditorPopup");
        if (actionGroup instanceof DefaultActionGroup) {
            DefaultActionGroup group = (DefaultActionGroup) actionGroup;
            group.removeAll();
            group.add(new NewChatAction());
            group.addSeparator();

            var configuredActions = ConfigurationState.getInstance().getTableData();
            configuredActions.forEach((label, prompt) -> {
                var action = new BaseEditorAction(label, label) {
                    @Override
                    protected void actionPerformed(Project project, Editor editor, String selectedText) {
                        var fileExtension = FileUtil.getFileExtension(((EditorImpl) editor).getVirtualFile().getName());
                        var message = new Message(prompt.replace("{{selectedCode}}", format("\n```%s\n%s\n```", fileExtension, selectedText)));
                        message.setUserMessage(Objects.requireNonNull(EditorActionEnum.getEnumByLabel(label)).getUserMessage());
                        var toolWindowContentManager = project.getService(StandardChatToolWindowContentManager.class);
                        toolWindowContentManager.getToolWindow().show();
                        message.setReferencedFilePaths(
                                Stream.ofNullable(project.getUserData(HiCodeAssistantKeys.SELECTED_FILES))
                                        .flatMap(Collection::stream)
                                        .map(ReferencedFile::getFilePath)
                                        .collect(toList()));
                        toolWindowContentManager.sendMessage(message);
                    }
                };
                group.add(action);
            });
        }
    }

    public static void registerOrReplaceAction(AnAction action) {
        ActionManager actionManager = ActionManager.getInstance();
        var actionId = convertToId(action.getTemplateText());
        if (actionManager.getAction(actionId) != null) {
            actionManager.replaceAction(actionId, action);
        } else {
            actionManager.registerAction(actionId, action, PluginId.getId("com.hisun.code-assistant"));
        }
    }

    public static String convertToId(String label) {
        return "hi-code-assistant." + CaseUtils.toCamelCase(label, true);
    }
}
