package com.hisun.codeassistant.actions.toolwindow;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class OpenInEditorAction extends AnAction {
    public OpenInEditorAction() {
        super(HiCodeAssistantBundle.get("action.editor.open"), HiCodeAssistantBundle.get("action.editor.open.desc"), AllIcons.Actions.SplitVertically);
        EditorActionsUtil.registerOrReplaceAction(this);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        var currentConversation = ConversationsState.getCurrentConversation();
        var isEnabled = currentConversation != null && !currentConversation.getMessages().isEmpty();
        event.getPresentation().setEnabled(isEnabled);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        var currentConversation = ConversationsState.getCurrentConversation();
        if (project != null && currentConversation != null) {
            var dateTimeStamp = currentConversation.getUpdatedOn()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            var fileName = format("%s_%s.md", currentConversation.getModel(), dateTimeStamp);
            var fileContent = currentConversation
                    .getMessages()
                    .stream()
                    .map(it -> format("### User:\n%s\n### HiCodeAssistant:\n%s\n", it.getUserMessage(),
                            it.getResponse()))
                    .collect(Collectors.joining());
            VirtualFile file = new LightVirtualFile(fileName, fileContent);
            FileEditorManager.getInstance(project).openFile(file, true);
            var toolWindow = requireNonNull(
                    ToolWindowManager.getInstance(project).getToolWindow("HiCodeAssistant"));
            toolWindow.hide();
        }
    }
}
