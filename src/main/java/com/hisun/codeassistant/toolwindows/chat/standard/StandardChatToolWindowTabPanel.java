package com.hisun.codeassistant.toolwindows.chat.standard;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.completions.ConversationType;
import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.toolwindows.chat.ChatToolWindowTabPanel;
import com.hisun.codeassistant.toolwindows.chat.ui.ChatMessageResponseBody;
import com.hisun.codeassistant.toolwindows.chat.ui.ResponsePanel;
import com.hisun.codeassistant.toolwindows.chat.ui.UserMessagePanel;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.hisun.codeassistant.utils.EditorUtil;
import com.hisun.codeassistant.utils.file.FileUtil;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static java.lang.String.format;

public class StandardChatToolWindowTabPanel extends ChatToolWindowTabPanel {
    public StandardChatToolWindowTabPanel(
            @NotNull Project project,
            @NotNull Conversation conversation) {
        super(project, conversation);
        if (conversation.getMessages().isEmpty()) {
            displayLandingView();
        } else {
            displayConversation(conversation);
        }
    }

    @Override
    protected JComponent getLandingView() {
        return new StandardChatToolWindowLandingPanel((action, locationOnScreen) -> {
            var editor = EditorUtil.getSelectedEditor(project);
            if (editor == null || !editor.getSelectionModel().hasSelection()) {
                OverlayUtil.showWarningBalloon(
                        editor == null ? HiCodeAssistantBundle.get("toolwindow.chat.standard.editor")
                                : HiCodeAssistantBundle.get("toolwindow.chat.standard.code"),
                        locationOnScreen);
                return;
            }

            var fileExtension = FileUtil.getFileExtension(
                    ((EditorImpl) editor).getVirtualFile().getName());
            var message = new Message(action.getPrompt().replace(
                    "{{selectedCode}}",
                    format("\n```%s\n%s\n```", fileExtension, editor.getSelectionModel().getSelectedText())));
            message.setUserMessage(action.getUserMessage());

            sendMessage(message, ConversationType.DEFAULT);
        });
    }

    private void displayConversation(@NotNull Conversation conversation) {
        clearWindow();
        conversation.getMessages().forEach(message -> {
            var messageResponseBody =
                    new ChatMessageResponseBody(project, this).withResponse(message.getResponse());

            var serpResults = message.getSerpResults();
            if (serpResults != null && !serpResults.isEmpty()) {
                messageResponseBody.displaySerpResults(serpResults);
            }
            messageResponseBody.hideCaret();

            var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
            messagePanel.add(new UserMessagePanel(project, message, this));
            messagePanel.add(new ResponsePanel()
                    .withReloadAction(() -> reloadMessage(message, conversation, ConversationType.DEFAULT))
                    .withDeleteAction(() -> removeMessage(message.getId(), conversation))
                    .addContent(messageResponseBody));
        });
    }
}
