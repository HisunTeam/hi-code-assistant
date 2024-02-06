package com.hisun.codeassistant.settings.state;

import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;

@Setter
@State(name = "HiCodeAssistant_GeneralSettings_0206", storages = @Storage("HiCodeAssistant_GeneralSettings_0206.xml"))
public class SettingsState implements PersistentStateComponent<SettingsState> {
    private String email = "";
    private String displayName = "";
    private boolean previouslySignedIn;
    private ServiceType selectedService = ServiceType.SELF_HOSTED;

    public SettingsState() {
    }

    public static SettingsState getInstance() {
        return ApplicationManager.getApplication().getService(SettingsState.class);
    }

    @Override
    public @Nullable SettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SettingsState settingsState) {
        XmlSerializerUtil.copyBean(settingsState, this);
    }

    public void sync(Conversation conversation) {
        var clientCode = conversation.getClientCode();
        if ("chat.completion".equals(clientCode)) {
            setSelectedService(ServiceType.OPENAI);
            OpenAISettingsState.getInstance().setModel(conversation.getModel());
        }
        if ("self.chat.completion".equals(clientCode)) {
            setSelectedService(ServiceType.SELF_HOSTED);
            SelfHostedLanguageModelSettingsState.getInstance().setModel(conversation.getModel());
        }
    }
    public String getModel() {
        return switch (selectedService) {
            case OPENAI -> OpenAISettingsState.getInstance().getModel();
            case SELF_HOSTED -> SelfHostedLanguageModelSettingsState.getInstance().getModel();
        };
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) {
            var systemUserName = System.getProperty("user.name");
            if (systemUserName == null || systemUserName.isEmpty()) {
                return "User";
            }
            return systemUserName;
        }
        return displayName;
    }

    public boolean isPreviouslySignedIn() {
        return previouslySignedIn;
    }

    public ServiceType getSelectedService() {
        return selectedService;
    }

}
