package com.hisun.codeassistant.actions.toolwindow;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ClearChatWindowAction extends AnAction {
    private final Runnable onActionPerformed;

    public ClearChatWindowAction(Runnable onActionPerformed) {
        super(HiCodeAssistantBundle.get("action.new.chat.clear"), HiCodeAssistantBundle.get("action.new.chat.clear.desc"), HiCodeAssistantIcons.CLEAR_ICON);
        this.onActionPerformed = onActionPerformed;
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
    public void actionPerformed(@NotNull AnActionEvent event) {
        onActionPerformed.run();
    }
}
