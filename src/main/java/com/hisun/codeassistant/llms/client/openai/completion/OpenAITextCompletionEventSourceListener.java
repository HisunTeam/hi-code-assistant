package com.hisun.codeassistant.llms.client.openai.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.codeassistant.llms.client.openai.api.CompletionResult;
import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import com.hisun.codeassistant.llms.completion.CompletionEventSourceListener;

public class OpenAITextCompletionEventSourceListener extends CompletionEventSourceListener {
    public OpenAITextCompletionEventSourceListener(CompletionEventListener listeners) {
        super(listeners);
    }

    protected String getMessage(String data) throws JsonProcessingException {
        var choice = new ObjectMapper()
                .readValue(data, CompletionResult.class)
                .getChoices()
                .get(0);
        if (choice != null) {
            return choice.getText();
        }
        return "";
    }

    @Override
    protected OpenAiError.OpenAiErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, OpenAiError.class).getError();
    }
}
