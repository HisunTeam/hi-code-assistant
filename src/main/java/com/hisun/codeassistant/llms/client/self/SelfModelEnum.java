package com.hisun.codeassistant.llms.client.self;

import lombok.Getter;

@Getter
public enum SelfModelEnum {
    ChatGLM3_6B("chatglm3-6b", "THUDM/chatglm3-6b", "ChatGLM3-6B (8k)", 8192),
    GPT_3_5_0125_16k("gpt-3.5-turbo-0125", "gpt-3.5-turbo-0125",  "GPT-3.5 Turbo (16k)", 16384),
    GPT_4_0125_128K("gpt-4-0125-preview", "gpt-4-0125-preview", "GPT-4 Turbo (128k)", 128000);
    // model name
    private final String name;

    // model code
    private final String code;

    // model display name
    private final String displayName;

    private final int maxTokens;

    SelfModelEnum(String name, String code, String displayName, int maxTokens) {
        this.name = name;
        this.code = code;
        this.displayName = displayName;
        this.maxTokens = maxTokens;
    }

    public static SelfModelEnum fromName(String name) {
        if (name == null) {
            return GPT_3_5_0125_16k;
        }
        for (SelfModelEnum type : SelfModelEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return GPT_3_5_0125_16k;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
