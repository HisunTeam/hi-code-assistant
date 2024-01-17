package com.hisun.codeassistant.settings.state;

import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.settings.service.ServiceSelectionForm;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@State(name = "HiCodeAssistant_OpenAISettings", storages = @Storage("HiCodeAssistant_OpenAISettings.xml"))
public class OpenAISettingsState implements PersistentStateComponent<OpenAISettingsState> {
    private static final String BASE_PATH = "/v1/chat/completions";

    private String organization = "";
    private String baseHost = "https://api.openai.com";
    private String path = BASE_PATH;
    private String model = OpenAIChatCompletionModel.GPT_3_5.getCode();
    private boolean openAIQuotaExceeded;

    public static OpenAISettingsState getInstance() {
        return ApplicationManager.getApplication().getService(OpenAISettingsState.class);
    }
    @Override
    public @Nullable OpenAISettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull OpenAISettingsState openAISettingsState) {
        XmlSerializerUtil.copyBean(openAISettingsState, this);
    }

    public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
        return !serviceSelectionForm.getOpenAIApiKey()
                .equals(OpenAICredentialsManager.getInstance().getApiKey())
                || !serviceSelectionForm.getOpenAIOrganization().equals(organization)
                || !serviceSelectionForm.getOpenAIBaseHost().equals(baseHost)
                || !serviceSelectionForm.getOpenAIPath().equals(path)
                || !serviceSelectionForm.getOpenAIModel().equals(model);
    }

    public void apply(ServiceSelectionForm serviceSelectionForm) {
        organization = serviceSelectionForm.getOpenAIOrganization();
        baseHost = serviceSelectionForm.getOpenAIBaseHost();
        path = serviceSelectionForm.getOpenAIPath();
        model = serviceSelectionForm.getOpenAIModel();
    }

    public void reset(ServiceSelectionForm serviceSelectionForm) {
        serviceSelectionForm.setOpenAIApiKey(OpenAICredentialsManager.getInstance().getApiKey());
        serviceSelectionForm.setOpenAIOrganization(organization);
        serviceSelectionForm.setOpenAIBaseHost(baseHost);
        serviceSelectionForm.setOpenAIPath(path);
        serviceSelectionForm.setOpenAIModel(model);
    }

    public boolean isUsingCustomPath() {
        return !BASE_PATH.equals(path);
    }

}
