package com.hisun.codeassistant.actions.toolbar;

import com.hisun.codeassistant.toolwindows.HiCodeAssistantChatToolWindowFactory;
import com.hisun.codeassistant.icons.HiCodeAssistantIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ToolbarClearAction extends AnAction {
    public ToolbarClearAction() {
        super("清除历史记录", "清除历史记录", HiCodeAssistantIcons.CLEAR_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (Objects.nonNull(project)) {
            HiCodeAssistantChatToolWindowFactory.getHiCodeAssistantChatToolWindow(project).clearSession();
        }
    }
}
