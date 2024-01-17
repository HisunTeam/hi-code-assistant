package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.llms.completion.SerpResult;

import java.util.List;

public interface CompletionResponseEventListener {
    default void handleMessage(String message) {
    }

    default void handleError(OpenAiError.OpenAiErrorDetails error, Throwable ex) {
    }

    default void handleTokensExceeded(Conversation conversation, Message message) {
    }

    default void handleCompleted(String fullMessage, CallParameters callParameters) {
    }

    default void handleSerpResults(List<SerpResult> results, Message message) {
    }
}
