package com.hisun.codeassistant.settings.state;

import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
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
@State(name = "HiCodeAssistant_SelfHostedLanguageModelSettings_0206", storages = @Storage("HiCodeAssistant_SelfHostedLanguageModelSettings_0206.xml"))
public class SelfHostedLanguageModelSettingsState implements PersistentStateComponent<SelfHostedLanguageModelSettingsState> {

    private static final String BASE_PATH = "/v1/chat/completions";
    private String baseHost = "http://10.9.50.190:8000";
    private String path = BASE_PATH;
    private String model = SelfModelEnum.GPT_3_5_1106_16k.getName();

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
//                || !serviceSelectionForm.getSelfHostedLanguageModelPath().equals(path)
                || !serviceSelectionForm.getSelfHostedLanguageModel().equals(model);
    }

    public void apply(ServiceSelectionForm serviceSelectionForm) {
        baseHost = serviceSelectionForm.getSelfHostedLanguageModelBaseHost();
//        path = serviceSelectionForm.getSelfHostedLanguageModelPath();
        model = serviceSelectionForm.getSelfHostedLanguageModel();
    }

    public void reset(ServiceSelectionForm serviceSelectionForm) {
        serviceSelectionForm.setSelfHostedLanguageModelBaseHost(baseHost);
//        serviceSelectionForm.setSelfHostedLanguageModelPath(path);
        serviceSelectionForm.setSelfHostedLanguageModel(model);
    }

    public boolean isUsingCustomPath() {
        return !BASE_PATH.equals(path);
    }

}
