package com.hisun.codeassistant.llms.client.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.hisun.codeassistant.llms.client.DeserializationUtil;
import com.hisun.codeassistant.llms.client.openai.api.ChatCompletionRequest;
import com.hisun.codeassistant.llms.client.openai.api.ChatCompletionResult;
import com.hisun.codeassistant.llms.client.openai.api.ChatFunctionCall;
import com.hisun.codeassistant.llms.client.openai.api.service.ChatCompletionRequestMixIn;
import com.hisun.codeassistant.llms.client.openai.api.service.ChatFunctionCallMixIn;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import lombok.Builder;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Builder
public class OpenAIClient {
    @Builder.Default
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    private final String apiKey;
    private final String organization;
    @Builder.Default
    private final String host = "https://api.openai.com";
    private static final ObjectMapper MAPPER = defaultObjectMapper();

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.addMixIn(ChatCompletionRequest.class, ChatCompletionRequestMixIn.class);
        mapper.addMixIn(ChatFunctionCall.class, ChatFunctionCallMixIn.class);
        return mapper;
    }
    public EventSource getChatCompletionAsync(ChatCompletionRequest request, CompletionEventListener eventListener) {
        return EventSources.createFactory(httpClient).newEventSource(
                buildCompletionRequest(request),
                new OpenAIChatCompletionEventSourceListener(eventListener));
    }

    public ChatCompletionResult getChatCompletion(ChatCompletionRequest request) {
        try (var response = httpClient.newCall(buildCompletionRequest(request)).execute()) {
            return DeserializationUtil.mapResponse(response, ChatCompletionResult.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Request buildCompletionRequest(ChatCompletionRequest completionRequest) {
        var headers = new HashMap<>(getRequiredHeaders());
        if (Boolean.TRUE.equals(completionRequest.getStream())) {
            headers.put("Accept", "text/event-stream");
        }
        try {
            return new Request.Builder()
                    .url(host + "/v1/chat/completions")
                    .headers(Headers.of(headers))
                    .post(RequestBody.create(
                            MAPPER.writeValueAsString(completionRequest),
                            MediaType.parse("application/json")))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to process request", e);
        }
    }

    private Map<String, String> getRequiredHeaders() {
        var headers = new HashMap<>(Map.of(
                "Authorization", "Bearer " + apiKey,
                "X-LLM-Application-Tag", "hi-code-assistant"));
        if (organization != null && !organization.isEmpty()) {
            headers.put("OpenAI-Organization", organization);
        }
        return headers;
    }
}
