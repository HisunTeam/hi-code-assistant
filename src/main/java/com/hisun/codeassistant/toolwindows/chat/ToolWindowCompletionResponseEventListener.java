package com.hisun.codeassistant.toolwindows.chat;

import com.hisun.codeassistant.EncodingManager;
import com.hisun.codeassistant.completions.CallParameters;
import com.hisun.codeassistant.completions.CompletionResponseEventListener;
import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.conversations.ConversationService;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.llms.completion.SerpResult;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.toolwindows.chat.ui.ChatMessageResponseBody;
import com.hisun.codeassistant.toolwindows.chat.ui.ResponsePanel;
import com.hisun.codeassistant.toolwindows.chat.ui.textarea.TotalTokensPanel;
import com.hisun.codeassistant.toolwindows.chat.ui.textarea.UserPromptTextArea;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.intellij.openapi.ui.Messages.OK;

abstract class ToolWindowCompletionResponseEventListener implements CompletionResponseEventListener {
    private static final Logger LOG = Logger.getInstance(
            ToolWindowCompletionResponseEventListener.class);

    private final StringBuilder messageBuilder = new StringBuilder();
    private final Map<UUID, List<SerpResult>> serpResultsMapping = new HashMap<>();
    private final EncodingManager encodingManager;
    private final ConversationService conversationService;
    private final ResponsePanel responsePanel;
    private final ChatMessageResponseBody responseContainer;
    private final TotalTokensPanel totalTokensPanel;
    private final UserPromptTextArea userPromptTextArea;

    private volatile boolean completed;

    public ToolWindowCompletionResponseEventListener(
            ConversationService conversationService,
            ResponsePanel responsePanel,
            TotalTokensPanel totalTokensPanel,
            UserPromptTextArea userPromptTextArea) {
        this.encodingManager = EncodingManager.getInstance();
        this.conversationService = conversationService;
        this.responsePanel = responsePanel;
        this.responseContainer = (ChatMessageResponseBody) responsePanel.getContent();
        this.totalTokensPanel = totalTokensPanel;
        this.userPromptTextArea = userPromptTextArea;
    }

    public abstract void handleTokensExceededPolicyAccepted();

    @Override
    public void handleMessage(String partialMessage) {
        try {
            ApplicationManager.getApplication()
                    .invokeLater(() -> {
                        responseContainer.update(partialMessage);
                        messageBuilder.append(partialMessage);

                        if (!completed) {
                            var ongoingTokens = encodingManager.countTokens(messageBuilder.toString());
                            totalTokensPanel.update(
                                    totalTokensPanel.getTokenDetails().getTotal() + ongoingTokens);
                        }
                    });
        } catch (Exception e) {
            responseContainer.displayError("Something went wrong.");
            throw new RuntimeException("Error while updating the content", e);
        }
    }

    @Override
    public void handleError(OpenAiError.OpenAiErrorDetails error, Throwable ex) {
        SwingUtilities.invokeLater(() -> {
            try {
                if ("insufficient_quota".equals(error.getCode())) {
                    responseContainer.displayQuotaExceeded();
                } else {
                    responseContainer.displayError(error.getMessage());
                }
            } finally {
                LOG.error(error.getMessage(), ex);
                responsePanel.enableActions();
                stopStreaming(responseContainer);
            }
        });
    }

    @Override
    public void handleTokensExceeded(Conversation conversation, Message message) {
        SwingUtilities.invokeLater(() -> {
            var answer = OverlayUtil.showTokenLimitExceededDialog();
            if (answer == OK) {
                conversationService.discardTokenLimits(conversation);
                handleTokensExceededPolicyAccepted();
            } else {
                stopStreaming(responseContainer);
            }
        });
    }

    @Override
    public void handleCompleted(String fullMessage, CallParameters callParameters) {
        var message = callParameters.getMessage();
        conversationService.saveMessage(fullMessage, callParameters);

        var serpResults = serpResultsMapping.get(message.getId());
        var containsResults = serpResults != null && !serpResults.isEmpty();
        if (containsResults) {
            message.setSerpResults(serpResults);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                responsePanel.enableActions();
                if (containsResults) {
                    responseContainer.displaySerpResults(serpResults);
                }
                totalTokensPanel.updateUserPromptTokens(userPromptTextArea.getText());
                totalTokensPanel.updateConversationTokens(callParameters.getConversation());
            } finally {
                stopStreaming(responseContainer);
            }
        });
    }

    @Override
    public void handleSerpResults(List<SerpResult> results, Message message) {
        serpResultsMapping.put(message.getId(), results);
    }

    private void stopStreaming(ChatMessageResponseBody responseContainer) {
        completed = true;
        userPromptTextArea.setSubmitEnabled(true);
        responseContainer.hideCaret();
    }
}
