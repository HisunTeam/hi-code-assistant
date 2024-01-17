package com.hisun.codeassistant.settings.state;

import com.hisun.codeassistant.enums.ModelEnum;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
@State(name = "HiCodeAssistant_Settings", storages = @Storage("HiCodeAssistant_Settings.xml"))
public class HiCodeAssistantSettingsState implements PersistentStateComponent<HiCodeAssistantSettingsState> {
    private String fullName;

    private String selectedModel = ModelEnum.CHATGLM3_6B.getName();

    private Map<String, String> modelBaseHostMap = new ConcurrentHashMap<>();

    public static HiCodeAssistantSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(HiCodeAssistantSettingsState.class);
    }

    public String getModeBaselHost(String selectedModel) {
        return modelBaseHostMap.getOrDefault(selectedModel, "");
    }

    public void setModelBaseHost(String model, String host) {
        this.modelBaseHostMap.put(model, host);
    }
    @Override
    public HiCodeAssistantSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull HiCodeAssistantSettingsState hiCodeAssistantSettingsState) {
        XmlSerializerUtil.copyBean(hiCodeAssistantSettingsState, this);
    }

    public String getFullName() {
        if (fullName == null || fullName.isEmpty()) {
            return System.getProperty("user.name", "User");
        }
        return fullName;
    }
}
