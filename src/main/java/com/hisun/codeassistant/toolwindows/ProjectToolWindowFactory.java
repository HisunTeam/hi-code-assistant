package com.hisun.codeassistant.toolwindows;

import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowPanel;
import com.hisun.codeassistant.toolwindows.conversations.ConversationsToolWindow;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ProjectToolWindowFactory implements ToolWindowFactory, DumbAware {
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var chatToolWindowPanel = new StandardChatToolWindowPanel(project, toolWindow.getDisposable());
        var conversationsToolWindow = new ConversationsToolWindow(project);

        addContent(toolWindow, chatToolWindowPanel, "Chat");
        addContent(toolWindow, conversationsToolWindow.getContent(), "Chat History");
        toolWindow.addContentManagerListener(new ContentManagerListener() {
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                var content = event.getContent();
                if ("Chat History".equals(content.getTabName()) && content.isSelected()) {
                    conversationsToolWindow.refresh();
                }
            }
        });
    }

    public void addContent(ToolWindow toolWindow, JComponent panel, String displayName) {
        var contentManager = toolWindow.getContentManager();
        contentManager.addContent(contentManager.getFactory().createContent(panel, displayName, false));
    }
}
