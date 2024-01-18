package com.hisun.codeassistant;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;

public class HiCodeAssistantBundle extends DynamicBundle {
    private static final HiCodeAssistantBundle INSTANCE = new HiCodeAssistantBundle();

    private static ResourceBundle bundleZH = ResourceBundle.getBundle("messages.hi-code-assistant", Locale.CHINA);

    private HiCodeAssistantBundle() {
        super("messages.hi-code-assistant");
    }

    public static String get(@NotNull @PropertyKey(resourceBundle = "messages.hi-code-assistant") String key){
        return bundleZH.getString(key);
    }

    public static String get(@NotNull @PropertyKey(resourceBundle = "messages.hi-code-assistant") String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

}
