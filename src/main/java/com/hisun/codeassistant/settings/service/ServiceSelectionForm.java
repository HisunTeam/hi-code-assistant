package com.hisun.codeassistant.settings.service;

import com.hisun.codeassistant.settings.service.openai.OpenAISettings;
import com.hisun.codeassistant.settings.service.openai.OpenAISettingsForm;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettings;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettingsForm;
import com.intellij.openapi.Disposable;

public class ServiceSelectionForm {
    private final OpenAISettingsForm openAISettingsForm;
    private final SelfHostedLanguageModelSettingsForm selfHostedLanguageModelSettingsForm;

    public ServiceSelectionForm() {
        openAISettingsForm = new OpenAISettingsForm(OpenAISettings.getCurrentState());
        selfHostedLanguageModelSettingsForm = new SelfHostedLanguageModelSettingsForm(SelfHostedLanguageModelSettings.getCurrentState());
    }

    public OpenAISettingsForm getOpenAISettingsForm() {
        return openAISettingsForm;
    }

    public SelfHostedLanguageModelSettingsForm getSelfHostedLanguageModelSettingsForm() {
        return selfHostedLanguageModelSettingsForm;
    }
    public void resetForms() {
        openAISettingsForm.resetForm();
        selfHostedLanguageModelSettingsForm.resetForm();
    }

}
