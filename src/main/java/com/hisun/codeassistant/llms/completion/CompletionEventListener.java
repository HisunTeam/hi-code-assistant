package com.hisun.codeassistant.llms.completion;

import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import okhttp3.sse.EventSource;

public interface CompletionEventListener<T> {
    default void onMessage(T message, EventSource eventSource) {
    }

    default void onComplete(StringBuilder messageBuilder) {
    }

    default void onCancelled(StringBuilder messageBuilder) {
    }

    default void onError(OpenAiError.OpenAiErrorDetails error, Throwable ex) {
    }
}
