package com.hisun.codeassistant.actions.editor;

import com.hisun.codeassistant.HiCodeAssistantKeys;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.embedding.ReferencedFile;
import com.hisun.codeassistant.settings.configuration.ConfigurationSettings;
import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowContentManager;
import com.hisun.codeassistant.utils.file.FileUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.text.CaseUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class EditorActionsUtil {

    public static ArrayList<EditorActionEnum> DEFAULT_ACTIONS = new ArrayList<>();

    static {
        DEFAULT_ACTIONS.add(EditorActionEnum.REVIEW);
        DEFAULT_ACTIONS.add(EditorActionEnum.REFACTOR);
        DEFAULT_ACTIONS.add(EditorActionEnum.PERFORMANCE);
        DEFAULT_ACTIONS.add(EditorActionEnum.SECURITY);
        DEFAULT_ACTIONS.add(EditorActionEnum.OPTIMIZE);
        DEFAULT_ACTIONS.add(EditorActionEnum.COMMENT);
        DEFAULT_ACTIONS.add(EditorActionEnum.EXPLAIN);
        DEFAULT_ACTIONS.add(EditorActionEnum.GENERATE_TESTS);
    }

    public static void refreshActions() {
        AnAction actionGroup =
                ActionManager.getInstance().getAction("HiCodeAssistantEditorPopup");
        if (actionGroup instanceof DefaultActionGroup) {
            DefaultActionGroup group = (DefaultActionGroup) actionGroup;
            group.removeAll();
            group.add(new NewChatAction());
            group.addSeparator();

            var configuredActions = ConfigurationSettings.getCurrentState().getTableData();
            configuredActions.forEach(actionEnum -> {
                var action = new BaseEditorAction(actionEnum.getLabel(), actionEnum.getLabel()) {
                    @Override
                    protected void actionPerformed(Project project, Editor editor, String selectedText) {
                        var fileExtension = FileUtil.getFileExtension(editor.getVirtualFile().getName());
                        var message = new Message(actionEnum.getPrompt().replace("{{selectedCode}}", format("\n```%s\n%s\n```", fileExtension, selectedText)));
                        message.setUserMessage(Objects.requireNonNull(EditorActionEnum.getEnumByLabel(actionEnum.getLabel())).getUserMessage());
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
                Optional.ofNullable(actionEnum.getKeyStroke()).ifPresent(
                        keyStroke -> KeymapManager.getInstance().getActiveKeymap().addShortcut(ActionManager.getInstance().getId(action), new KeyboardShortcut(keyStroke, null))
                );

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
