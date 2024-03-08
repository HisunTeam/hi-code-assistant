package com.hisun.codeassistant.settings.service;

import com.hisun.codeassistant.HiCodeAssistantBundle;

public enum CodeCompletionModel {
    OPENAI("gpt-3.5-turbo-instruct", "gpt-3.5-turbo-instruct"),
    ChatGLM3_6B("chatglm3-6b", "THUDM/chatglm3-6b/Completions-API");

    private final String name;
    private final String model;


    CodeCompletionModel(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public String getName() {
        return name;
    }
    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return name;
    }
}
