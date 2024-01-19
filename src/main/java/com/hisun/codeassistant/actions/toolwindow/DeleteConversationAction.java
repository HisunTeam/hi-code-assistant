package com.hisun.codeassistant.actions.toolwindow;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeleteConversationAction extends AnAction {
    private final Runnable onDelete;

    public DeleteConversationAction(Runnable onDelete) {
        super(HiCodeAssistantBundle.get("action.conversation.delete"), HiCodeAssistantBundle.get("action.conversation.delete.desc"), AllIcons.Actions.GC);
        this.onDelete = onDelete;
        EditorActionsUtil.registerOrReplaceAction(this);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(ConversationsState.getCurrentConversation() != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (OverlayUtil.showDeleteConversationDialog() == Messages.YES) {
            var project = event.getProject();
            if (project != null) {
                onDelete.run();
            }
        }
    }
}
