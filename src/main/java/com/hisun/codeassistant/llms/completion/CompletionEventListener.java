package com.hisun.codeassistant.llms.completion;

import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;

public interface CompletionEventListener {
    default void onMessage(String message) {
    }

    default void onComplete(StringBuilder messageBuilder) {
    }

    default void onError(OpenAiError.OpenAiErrorDetails error, Throwable ex) {
    }
}
