package com.hisun.codeassistant.settings.state;

import com.hisun.codeassistant.enums.ModelEnum;
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
@State(name = "HiCodeAssistant_SelfHostedLanguageModelSettings", storages = @Storage("HiCodeAssistant_SelfHostedLanguageModelSettings.xml"))
public class SelfHostedLanguageModelSettingsState implements PersistentStateComponent<SelfHostedLanguageModelSettingsState> {

    private static final String BASE_PATH = "/v1/chat/completions";
    private String baseHost = "";
    private String path = BASE_PATH;
    private String model = ModelEnum.ChatGLM3_6B.getName();

    public static SelfHostedLanguageModelSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(SelfHostedLanguageModelSettingsState.class);
    }

    @Override
    public @Nullable SelfHostedLanguageModelSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SelfHostedLanguageModelSettingsState selfHostedLanguageModelSettingsState) {
        XmlSerializerUtil.copyBean(selfHostedLanguageModelSettingsState, this);
    }

    public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
        return !serviceSelectionForm.getSelfHostedLanguageModelBaseHost().equals(baseHost)
                || !serviceSelectionForm.getSelfHostedLanguageModelPath().equals(path)
                || !serviceSelectionForm.getSelfHostedLanguageModel().equals(model);
    }

    public void apply(ServiceSelectionForm serviceSelectionForm) {
        baseHost = serviceSelectionForm.getSelfHostedLanguageModelBaseHost();
        path = serviceSelectionForm.getSelfHostedLanguageModelPath();
        model = serviceSelectionForm.getSelfHostedLanguageModel();
    }

    public void reset(ServiceSelectionForm serviceSelectionForm) {
        serviceSelectionForm.setSelfHostedLanguageModelBaseHost(baseHost);
        serviceSelectionForm.setSelfHostedLanguageModelPath(path);
        serviceSelectionForm.setSelfHostedLanguageModel(model);
    }

    public boolean isUsingCustomPath() {
        return !BASE_PATH.equals(path);
    }

}
