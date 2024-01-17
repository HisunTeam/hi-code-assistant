package com.hisun.codeassistant.toolwindows.chat.standard;

import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.completions.ConversationType;
import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.conversations.ConversationService;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service(Service.Level.PROJECT)
public final class StandardChatToolWindowContentManager {
    private final Project project;

    public StandardChatToolWindowContentManager(Project project) {
        this.project = project;
    }

    public void sendMessage(Message message) {
        sendMessage(message, ConversationType.DEFAULT);
    }

    public void sendMessage(Message message, ConversationType conversationType) {
        getToolWindow().show();

        if (ConfigurationState.getInstance().isCreateNewChatOnEachAction()
                || ConversationsState.getCurrentConversation() == null) {
            createNewTabPanel().sendMessage(message, conversationType);
            return;
        }

        tryFindChatTabbedPane()
                .map(tabbedPane -> tabbedPane.tryFindActiveTabPanel().orElseGet(this::createNewTabPanel))
                .orElseGet(this::createNewTabPanel)
                .sendMessage(message, conversationType);
    }

    public void displayConversation(@NotNull Conversation conversation) {
        displayChatTab();
        tryFindChatTabbedPane()
                .ifPresent(tabbedPane -> tabbedPane.tryFindTabTitle(conversation.getId())
                        .ifPresentOrElse(
                                title -> tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(title)),
                                () -> tabbedPane.addNewTab(
                                        new StandardChatToolWindowTabPanel(project, conversation))));
    }

    public StandardChatToolWindowTabPanel createNewTabPanel() {
        displayChatTab();
        return tryFindChatTabbedPane()
                .map(item -> {
                    var panel = new StandardChatToolWindowTabPanel(
                            project,
                            ConversationService.getInstance().startConversation());
                    item.addNewTab(panel);
                    return panel;
                })
                .orElseThrow();
    }

    public void displayChatTab() {
        var toolWindow = getToolWindow();
        toolWindow.show();

        var contentManager = toolWindow.getContentManager();
        tryFindFirstChatTabContent().ifPresentOrElse(
                contentManager::setSelectedContent,
                () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
        );
    }

    public Optional<StandardChatToolWindowTabbedPane> tryFindChatTabbedPane() {
        var chatTabContent = tryFindFirstChatTabContent();
        if (chatTabContent.isPresent()) {
            var chatToolWindowPanel = (StandardChatToolWindowPanel) chatTabContent.get().getComponent();
            return Optional.of(chatToolWindowPanel.getChatTabbedPane());
        }
        return Optional.empty();
    }

    public void resetAll() {
        tryFindChatTabbedPane().ifPresent(tabbedPane -> {
            tabbedPane.clearAll();
            tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(
                    project,
                    ConversationService.getInstance().startConversation()));
        });
    }

    public @NotNull ToolWindow getToolWindow() {
        var toolWindowManager = ToolWindowManager.getInstance(project);
        var toolWindow = toolWindowManager.getToolWindow("HiCodeAssistant");
        if (toolWindow == null) {
            // https://intellij-support.jetbrains.com/hc/en-us/community/posts/11533368171026/comments/11538403084562
            return toolWindowManager
                    .registerToolWindow(RegisterToolWindowTask.closable(
                            "HiCodeAssistant",
                            () -> "HiCodeAssistant",
                            HiCodeAssistantIcons.SYSTEM_ICON,
                            ToolWindowAnchor.RIGHT));
        }
        return toolWindow;
    }

    private Optional<Content> tryFindFirstChatTabContent() {
        return Arrays.stream(getToolWindow().getContentManager().getContents())
                .filter(content -> "Chat".equals(content.getTabName()))
                .findFirst();
    }
}
