package com.hisun.codeassistant.codecompletions;

import com.hisun.codeassistant.llms.client.openai.api.CompletionRequest;

public class CodeCompletionRequestProvider {
    private static final int MAX_TOKENS = 128;

    private final InfillRequestDetails details;

    public CodeCompletionRequestProvider(InfillRequestDetails details) {
        this.details = details;
    }

    public CompletionRequest buildOpenAIRequest() {
        return CompletionRequest.builder()
                .prompt(details.getPrefix())
                .stream(true)
                .maxTokens(MAX_TOKENS)
                .temperature(0.1)
                .build();
    }

    public CompletionRequest buildSelfRequest() {
        return CompletionRequest.builder()
                .prompt(details.getPrefix())
                .stream(true)
                .maxTokens(MAX_TOKENS)
                .temperature(0.1)
                .build();
    }
}
