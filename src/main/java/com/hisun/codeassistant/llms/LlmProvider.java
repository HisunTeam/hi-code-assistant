package com.hisun.codeassistant.llms;

import com.hisun.codeassistant.llms.openai.api.ChatCompletionRequest;

public interface LlmProvider {
    String chatCompletion(ChatCompletionRequest chatCompletionRequest);
}
