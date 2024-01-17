package com.hisun.codeassistant.toolwindows.chat.ui.textarea;

import com.hisun.codeassistant.EncodingManager;
import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import lombok.Getter;

@Getter
public class TotalTokensDetails {
    private final int systemPromptTokens;
    private int conversationTokens;
    private int userPromptTokens;
    private int highlightedTokens;

    public TotalTokensDetails(EncodingManager encodingManager) {
        systemPromptTokens = encodingManager.countTokens(
                ConfigurationState.getInstance().getSystemPrompt());
    }

    public void setConversationTokens(int conversationTokens) {
        this.conversationTokens = conversationTokens;
    }

    public void setUserPromptTokens(int userPromptTokens) {
        this.userPromptTokens = userPromptTokens;
    }

    public void setHighlightedTokens(int highlightedTokens) {
        this.highlightedTokens = highlightedTokens;
    }

    public int getTotal() {
        return systemPromptTokens
                + conversationTokens
                + userPromptTokens
                + highlightedTokens;
    }
}
