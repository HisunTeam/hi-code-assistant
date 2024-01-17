package com.hisun.codeassistant.llms.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.hisun.codeassistant.llms.client.openai.api.ChatCompletionRequest;
import com.hisun.codeassistant.llms.client.openai.api.ChatFunctionCall;
import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.llms.client.openai.api.service.ChatCompletionRequestMixIn;
import com.hisun.codeassistant.llms.client.openai.api.service.ChatFunctionCallMixIn;
import okhttp3.Response;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

import static java.lang.String.format;

public abstract class CompletionEventSourceListener extends EventSourceListener {
    private static final Logger LOG = LoggerFactory.getLogger(CompletionEventSourceListener.class);

    private final CompletionEventListener listeners;
    private final StringBuilder messageBuilder = new StringBuilder();
    private final boolean retryOnReadTimeout;
    private final Consumer<String> onRetry;
    public static final ObjectMapper MAPPER = defaultObjectMapper();

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.addMixIn(ChatCompletionRequest.class, ChatCompletionRequestMixIn.class);
        mapper.addMixIn(ChatFunctionCall.class, ChatFunctionCallMixIn.class);
        return mapper;
    }

    public CompletionEventSourceListener(CompletionEventListener listeners) {
        this(listeners, false, null);
    }

    public CompletionEventSourceListener(CompletionEventListener listeners,
                                         boolean retryOnReadTimeout, Consumer<String> onRetry) {
        this.listeners = listeners;
        this.retryOnReadTimeout = retryOnReadTimeout;
        this.onRetry = onRetry;
    }

    protected abstract String getMessage(String data) throws JsonProcessingException;

    protected abstract OpenAiError.OpenAiErrorDetails getErrorDetails(String data) throws JsonProcessingException;

    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        LOG.info("Request opened.");
    }

    public void onClosed(@NotNull EventSource eventSource) {
        LOG.info("Request closed.");
        listeners.onComplete(messageBuilder);
    }

    public void onEvent(
            @NotNull EventSource eventSource,
            String id,
            String type,
            @NotNull String data) {
        try {
            // Redundant end signal so just ignore
            if ("[DONE]".equals(data)) {
                return;
            }

            var message = getMessage(data);
            if (message != null) {
                messageBuilder.append(message);
                listeners.onMessage(message);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to deserialize payload.", e);
        }
    }

    public void onFailure(
            @NotNull EventSource eventSource,
            Throwable throwable,
            Response response) {
        if (throwable instanceof StreamResetException
                || (throwable instanceof SocketException
                && "Socket closed".equals(throwable.getMessage()))) {
            LOG.info("Stream was cancelled");
            listeners.onComplete(messageBuilder);
            return;
        }

        if (throwable instanceof SocketTimeoutException) {
            if (retryOnReadTimeout) {
                LOG.info("Retrying request.");
                onRetry.accept(messageBuilder.toString());
                return;
            }

            listeners.onError(new OpenAiError.OpenAiErrorDetails("Request timed out. This may be due to the server being overloaded.", null, null, null), throwable);
            return;
        }

        try {
            if (response == null) {
                listeners.onError(new OpenAiError.OpenAiErrorDetails(throwable.getMessage(), null, null, null), throwable);
                return;
            }

            var body = response.body();
            if (body != null) {
                var jsonBody = body.string();
                try {
                    var errorDetails = getErrorDetails(jsonBody);
                    if (errorDetails == null
                            || errorDetails.getMessage() == null
                            || errorDetails.getMessage().isEmpty()) {
                        listeners.onError(toUnknownErrorResponse(response, jsonBody), new RuntimeException());
                    } else {
                        listeners.onError(errorDetails, new RuntimeException());
                    }
                } catch (JsonProcessingException e) {
                    LOG.error("Could not serialize error response", throwable);
                    listeners.onError(toUnknownErrorResponse(response, jsonBody), e);
                }
            }
        } catch (IOException ex) {
            listeners.onError(new OpenAiError.OpenAiErrorDetails(ex.getMessage(), null, null, null), ex);
        }
    }

    private OpenAiError.OpenAiErrorDetails toUnknownErrorResponse(Response response, String jsonBody) {
        return new OpenAiError.OpenAiErrorDetails(format("Unknown API response. Code: %s, Body: %s", response.code(), jsonBody), null, null, null);
    }
}
