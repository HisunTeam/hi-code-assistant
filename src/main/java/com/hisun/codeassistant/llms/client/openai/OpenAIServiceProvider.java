package com.hisun.codeassistant.llms.client.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.codeassistant.llms.client.openai.api.ChatMessageRole;
import com.hisun.codeassistant.llms.client.openai.api.*;
import com.intellij.openapi.components.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Objects;

@Service(Service.Level.PROJECT)
public final class OpenAIServiceProvider implements LlmProvider {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String chatCompletion(ChatCompletionRequest chatCompletionRequest) {
        var settings = HiCodeAssistantSettingsState.getInstance();

        var host = settings.getModeBaselHost(settings.getSelectedModel());

        if (StringUtils.isEmpty(host)) {
            return "Chat completion failed: host is empty";
        }

        var modelName = HiCodeAssistantSettingsState.getInstance().getSelectedModel();

        if (StringUtils.isEmpty(modelName)) {
            return "Chat completion failed: openai model name is empty";
        }

        chatCompletionRequest.setModel(modelName);

        OpenAiAPI openAiAPI = new OpenAiAPI();
        try {
            HttpResponse<String> response = openAiAPI.postToOpenAiApi(objectMapper.writeValueAsString(chatCompletionRequest), host);
            return parseResult(chatCompletionRequest, response);
        } catch (Exception e) {
            return "Chat completion failed: " + e.getMessage();
        }
    }

    private String parseResult(ChatCompletionRequest chatCompletionRequest, HttpResponse<String> response) throws IOException {
        if (response == null) {
            return "无响应，请重试";
        }

        var result = Objects.requireNonNull(response.body());

        if (response.statusCode() == 200) {
            ChatMessage message = objectMapper.readValue(result, ChatCompletionResult.class)
                    .getChoices()
                    .get(0)
                    .getMessage();
            // multi chat message
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), message.getContent());
            chatCompletionRequest.getMessages().add(chatMessage);
            return message.getContent();

        } else {
            return objectMapper.readValue(result, OpenAiError.class)
                    .getError()
                    .getMessage();
        }
    }
}
