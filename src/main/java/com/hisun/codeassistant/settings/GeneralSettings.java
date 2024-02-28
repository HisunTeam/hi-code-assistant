package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.service.openai.OpenAISettings;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "HiCodeAssistant_GeneralSettings", storages = @Storage("HiCodeAssistant_GeneralSettings.xml"))
public class GeneralSettings implements PersistentStateComponent<GeneralSettingsState> {
    private GeneralSettingsState state = new GeneralSettingsState();

    @Override
    @NotNull
    public GeneralSettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull GeneralSettingsState state) {
        this.state = state;
    }

    public static GeneralSettingsState getCurrentState() {
        return getInstance().getState();
    }

    public static GeneralSettings getInstance() {
        return ApplicationManager.getApplication().getService(GeneralSettings.class);
    }

    public void sync(Conversation conversation) {
        var clientCode = conversation.getClientCode();
        if ("chat.completion".equals(clientCode)) {
            state.setSelectedService(ServiceType.OPENAI);
            OpenAISettings.getCurrentState().setModel(conversation.getModel());
        }
        if ("self.chat.completion".equals(clientCode)) {
            state.setSelectedService(ServiceType.SELF_HOSTED);
        }
    }

    public String getModel() {
        return switch (state.getSelectedService()) {
            case OPENAI -> OpenAISettings.getCurrentState().getModel();
            case SELF_HOSTED -> SelfHostedLanguageModelSettings.getCurrentState().getModel();
        };
    }
}
