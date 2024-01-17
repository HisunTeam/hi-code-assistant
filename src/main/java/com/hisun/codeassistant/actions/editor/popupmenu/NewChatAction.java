package com.hisun.codeassistant.actions.editor.popupmenu;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class NewChatAction extends AnAction {
    public NewChatAction() {
        super("新建HiCodeAssistant会话", "打开新的HiCodeAssistant会话", AllIcons.Actions.Find);
        PopupMenuEditorActionGroupUtil.registerOrReplaceAction(this);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(e.getProject() != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        var project = anActionEvent.getProject();
        if (project != null) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("HiCodeAssistant");
            if (toolWindow == null) {
                return;
            }
            toolWindow.show();
        }
    }
}
