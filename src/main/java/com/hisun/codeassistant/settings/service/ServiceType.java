package com.hisun.codeassistant.settings.service;

import com.hisun.codeassistant.HiCodeAssistantBundle;

public enum ServiceType {
    OPENAI("OPENAI", HiCodeAssistantBundle.get("service.openai.title"), "chat.completion"),
    SELF_HOSTED("SELF-HOSTED_LANGUAGE_MODEL", HiCodeAssistantBundle.get("service.self.title"), "self.chat.completion");

    private final String code;
    private final String label;
    private final String completionCode;

    ServiceType(String code, String label, String completionCode) {
        this.code = code;
        this.label = label;
        this.completionCode = completionCode;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getCompletionCode() {
        return completionCode;
    }

    @Override
    public String toString() {
        return label;
    }
}
