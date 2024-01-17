package com.hisun.codeassistant;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class HiCodeAssistantBundle extends DynamicBundle {
    private static final HiCodeAssistantBundle INSTANCE = new HiCodeAssistantBundle();

    private HiCodeAssistantBundle() {
        super("messages.hi-code-assistant");
    }

    public static String get(@NotNull @PropertyKey(resourceBundle = "messages.hi-code-assistant") String key){
        return INSTANCE.getMessage(key);
    }

    public static String get(@NotNull @PropertyKey(resourceBundle = "messages.hi-code-assistant") String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

}
