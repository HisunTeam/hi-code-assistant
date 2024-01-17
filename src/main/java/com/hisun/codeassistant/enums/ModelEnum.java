package com.hisun.codeassistant.enums;

import lombok.Getter;

public enum ModelEnum {
    CHATGLM3_6B("chatglm3-6b", "THUDM/chatglm3-6b", "chatglm3-6b");
    // model name
    @Getter
    private final String name;

    // model code
    @Getter
    private final String code;

    // model display name
    private final String displayName;

    ModelEnum(String name, String code, String displayName) {
        this.name = name;
        this.code = code;
        this.displayName = displayName;
    }

    public static ModelEnum fromName(String name) {
        if (name == null) {
            return CHATGLM3_6B;
        }
        for (ModelEnum type : ModelEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return CHATGLM3_6B;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
