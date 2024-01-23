package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SelfHostedLanguageModelSettingsState;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowContentManager;
import com.hisun.codeassistant.utils.ApplicationUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsConfigurable implements Configurable {

    private Disposable parentDisposable;

    private SettingsComponent settingsComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return HiCodeAssistantBundle.get("settings.displayName");
    }

    @Override
    public @Nullable JComponent createComponent() {
        var settings = SettingsState.getInstance();
        parentDisposable = Disposer.newDisposable();
        settingsComponent = new SettingsComponent(parentDisposable, settings);
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        var settings = SettingsState.getInstance();
        var openAISettings = OpenAISettingsState.getInstance();
        var selfHostedLanguageModelSettings = SelfHostedLanguageModelSettingsState.getInstance();

        var serviceSelectionForm = settingsComponent.getServiceSelectionForm();
        return !settingsComponent.getDisplayName().equals(settings.getDisplayName())
                || isServiceChanged(settings)
                || openAISettings.isModified(serviceSelectionForm)
                || selfHostedLanguageModelSettings.isModified(serviceSelectionForm);
    }

    @Override
    public void apply() throws ConfigurationException {
        var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

        var prevKey = OpenAICredentialsManager.getInstance().getApiKey();
        if (prevKey != null && !prevKey.equals(serviceSelectionForm.getOpenAIApiKey())) {
            OpenAISettingsState.getInstance().setOpenAIQuotaExceeded(false);
        }

        OpenAICredentialsManager.getInstance().setApiKey(serviceSelectionForm.getOpenAIApiKey());

        var settings = SettingsState.getInstance();
        settings.setDisplayName(settingsComponent.getDisplayName());
        settings.setSelectedService(settingsComponent.getSelectedService());

        var selfHostedLanguageModelSettings = SelfHostedLanguageModelSettingsState.getInstance();
        var openAISettings = OpenAISettingsState.getInstance();
        openAISettings.apply(serviceSelectionForm);
        selfHostedLanguageModelSettings.apply(serviceSelectionForm);

        var serviceChanged = isServiceChanged(settings);
        var modelChanged = !openAISettings.getModel().equals(serviceSelectionForm.getOpenAIModel());
        if (serviceChanged || modelChanged) {
            resetActiveTab();
        }
    }

    @Override
    public void reset() {
        var settings = SettingsState.getInstance();
        var serviceSelectionForm = settingsComponent.getServiceSelectionForm();

        settingsComponent.setDisplayName(settings.getDisplayName());
        settingsComponent.setSelectedService(settings.getSelectedService());

        OpenAISettingsState.getInstance().reset(serviceSelectionForm);
        SelfHostedLanguageModelSettingsState.getInstance().reset(serviceSelectionForm);
    }

    @Override
    public void disposeUIResources() {
        if (parentDisposable != null) {
            Disposer.dispose(parentDisposable);
        }
        settingsComponent = null;
    }

    private boolean isServiceChanged(SettingsState settings) {
        return settingsComponent.getSelectedService() != settings.getSelectedService();
    }

    private void resetActiveTab() throws ConfigurationException {
        ConversationsState.getInstance().setCurrentConversation(null);
        var project = ApplicationUtil.findCurrentProject();
        if (project == null) {
            throw new ConfigurationException("Could not find current project.");
        }

        project.getService(StandardChatToolWindowContentManager.class).resetAll();
    }
}
