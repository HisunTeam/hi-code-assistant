package com.hisun.codeassistant.settings.service.self;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "HiCodeAssistant_SelfHostedLanguageModelSettings", storages = @Storage("HiCodeAssistant_SelfHostedLanguageModelSettings.xml"))
public class SelfHostedLanguageModelSettings implements PersistentStateComponent<SelfHostedLanguageModelSettingsState> {
    private SelfHostedLanguageModelSettingsState state = new SelfHostedLanguageModelSettingsState();

    @Override
    @NotNull
    public SelfHostedLanguageModelSettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull SelfHostedLanguageModelSettingsState state) {
        this.state = state;
    }

    public static SelfHostedLanguageModelSettingsState getCurrentState() {
        return getInstance().getState();
    }

    public static SelfHostedLanguageModelSettings getInstance() {
        return ApplicationManager.getApplication().getService(SelfHostedLanguageModelSettings.class);
    }

    public boolean isModified(SelfHostedLanguageModelSettingsForm form) {
        return !form.getCurrentState().equals(state);
    }
}
