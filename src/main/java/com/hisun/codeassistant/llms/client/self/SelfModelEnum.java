package com.hisun.codeassistant.llms.client.self;

import lombok.Getter;

@Getter
public enum SelfModelEnum {
    ChatGLM3_6B("chatglm3-6b", "THUDM/chatglm3-6b", "ChatGLM3-6B(8k)", 8192),
    GPT3_5("gpt-3.5", "gpt-3.5-turbo", "GPT-3.5(4k)", 4096);
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
            return ChatGLM3_6B;
        }
        for (SelfModelEnum type : SelfModelEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return ChatGLM3_6B;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
