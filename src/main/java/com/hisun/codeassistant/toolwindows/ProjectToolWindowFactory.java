package com.hisun.codeassistant.toolwindows;

import com.hisun.codeassistant.HiCodeAssistantBundle;
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

        addContent(toolWindow, chatToolWindowPanel, HiCodeAssistantBundle.get("toolwindow.chat.panel"));
        addContent(toolWindow, conversationsToolWindow.getContent(), HiCodeAssistantBundle.get("toolwindow.chat.history.panel"));
        toolWindow.addContentManagerListener(new ContentManagerListener() {
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                var content = event.getContent();
                if (HiCodeAssistantBundle.get("toolwindow.chat.history.panel").equals(content.getTabName()) && content.isSelected()) {
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
