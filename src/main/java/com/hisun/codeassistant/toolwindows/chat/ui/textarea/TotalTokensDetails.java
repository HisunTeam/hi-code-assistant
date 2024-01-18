package com.hisun.codeassistant.toolwindows.chat.ui.textarea;

import com.hisun.codeassistant.EncodingManager;
import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TotalTokensDetails {
    private final int systemPromptTokens;
    @Setter
    private int conversationTokens;
    @Setter
    private int userPromptTokens;
    @Setter
    private int highlightedTokens;
    @Setter
    private int referencedFilesTokens;

    public TotalTokensDetails(EncodingManager encodingManager) {
        systemPromptTokens = encodingManager.countTokens(
                ConfigurationState.getInstance().getSystemPrompt());
    }

    public int getTotal() {
        return systemPromptTokens
                + conversationTokens
                + userPromptTokens
                + highlightedTokens
                + referencedFilesTokens;
    }
}
