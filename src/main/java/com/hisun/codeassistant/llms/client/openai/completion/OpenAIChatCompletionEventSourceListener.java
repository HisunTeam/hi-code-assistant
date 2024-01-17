package com.hisun.codeassistant.llms.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.codeassistant.llms.client.openai.api.ChatCompletionResult;
import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import com.hisun.codeassistant.llms.completion.CompletionEventSourceListener;
import com.thoughtworks.xstream.mapper.Mapper;

public class OpenAIChatCompletionEventSourceListener extends CompletionEventSourceListener {
    public OpenAIChatCompletionEventSourceListener(CompletionEventListener listeners) {
        super(listeners);
    }

    protected String getMessage(String data) throws JsonProcessingException {
        var choice = MAPPER.readValue(data, ChatCompletionResult.class)
                .getChoices()
                .get(0);
        if (choice != null) {
            var delta = choice.getMessage();
            if (delta != null) {
                return delta.getContent();
            }
        }
        return "";
    }

    @Override
    protected OpenAiError.OpenAiErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return MAPPER.readValue(data, OpenAiError.class).getError();
    }
}
