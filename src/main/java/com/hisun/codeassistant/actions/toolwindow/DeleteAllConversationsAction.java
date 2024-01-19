package com.hisun.codeassistant.actions.toolwindow;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import com.hisun.codeassistant.conversations.ConversationService;
import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowContentManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeleteAllConversationsAction extends AnAction {
    private final Runnable onRefresh;

    public DeleteAllConversationsAction(Runnable onRefresh) {
        super(HiCodeAssistantBundle.get("action.new.chat.delete"), HiCodeAssistantBundle.get("action.new.chat.delete.desc"), AllIcons.Actions.GC);
        this.onRefresh = onRefresh;
        EditorActionsUtil.registerOrReplaceAction(this);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        var project = event.getProject();
        if (project != null) {
            var sortedConversations = ConversationService.getInstance().getSortedConversations();
            event.getPresentation().setEnabled(!sortedConversations.isEmpty());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        int answer = Messages.showYesNoDialog(
                HiCodeAssistantBundle.get("action.new.chat.delete.message"),
                HiCodeAssistantBundle.get("action.new.chat.delete.title"),
                HiCodeAssistantIcons.SYSTEM_ICON);
        if (answer == Messages.YES) {
            var project = event.getProject();
            if (project != null) {
                ConversationService.getInstance().clearAll();
                project.getService(StandardChatToolWindowContentManager.class).resetAll();
            }
            this.onRefresh.run();
        }
    }
}
