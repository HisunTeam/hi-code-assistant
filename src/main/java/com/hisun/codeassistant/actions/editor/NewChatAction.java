package com.hisun.codeassistant.actions.editor;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowContentManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class NewChatAction extends AnAction {
    public NewChatAction() {
        super(HiCodeAssistantBundle.get("action.new.chat"), HiCodeAssistantBundle.get("action.new.chat.desc"), AllIcons.Actions.Find);
        EditorActionsUtil.registerOrReplaceAction(this);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(event.getProject() != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var project = event.getProject();
        if (project != null) {
            ConversationsState.getInstance().setCurrentConversation(null);
            var tabPanel = project.getService(StandardChatToolWindowContentManager.class).createNewTabPanel();
            if (tabPanel != null) {
                tabPanel.displayLandingView();
            }
        }
    }
}
