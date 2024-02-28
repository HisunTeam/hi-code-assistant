package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.credentials.OpenAICredentialManager;
import com.hisun.codeassistant.settings.service.openai.OpenAISettings;
import com.hisun.codeassistant.settings.service.openai.OpenAISettingsForm;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettings;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettingsForm;
import com.hisun.codeassistant.toolwindows.chat.standard.StandardChatToolWindowContentManager;
import com.hisun.codeassistant.utils.ApplicationUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GeneralSettingsConfigurable implements Configurable {
    private Disposable parentDisposable;

    private GeneralSettingsComponent component;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return HiCodeAssistantBundle.get("settings.displayName");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return component.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        var settings = GeneralSettings.getInstance();
        parentDisposable = Disposer.newDisposable();
        component = new GeneralSettingsComponent(parentDisposable, settings);
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        var settings = GeneralSettings.getCurrentState();
        var serviceSelectionForm = component.getServiceSelectionForm();
        return !component.getDisplayName().equals(settings.getDisplayName())
                || component.getSelectedService() != settings.getSelectedService()
                || OpenAISettings.getInstance().isModified(serviceSelectionForm.getOpenAISettingsForm())
                || SelfHostedLanguageModelSettings.getInstance().isModified(serviceSelectionForm.getSelfHostedLanguageModelSettingsForm());
    }

    @Override
    public void apply() {
        var settings = GeneralSettings.getCurrentState();
        settings.setDisplayName(component.getDisplayName());
        settings.setSelectedService(component.getSelectedService());

        var serviceSelectionForm = component.getServiceSelectionForm();
        var openAISettingsForm = serviceSelectionForm.getOpenAISettingsForm();
        applyOpenAISettings(openAISettingsForm);
        applySelfHostedLanguageModelSettings(serviceSelectionForm.getSelfHostedLanguageModelSettingsForm());

        var serviceChanged = component.getSelectedService() != settings.getSelectedService();
        var modelChanged = !OpenAISettings.getCurrentState().getModel().equals(openAISettingsForm.getModel());
        if (serviceChanged || modelChanged) {
            resetActiveTab();
        }
    }

    private void applyOpenAISettings(OpenAISettingsForm form) {
        OpenAICredentialManager.getInstance().setCredential(form.getApiKey());
        OpenAISettings.getInstance().loadState(form.getCurrentState());
    }

    private void applySelfHostedLanguageModelSettings(SelfHostedLanguageModelSettingsForm form) {
        SelfHostedLanguageModelSettings.getInstance().loadState(form.getCurrentState());
    }

    @Override
    public void reset() {
        var settings = GeneralSettings.getCurrentState();
        component.setDisplayName(settings.getDisplayName());
        component.setSelectedService(settings.getSelectedService());
        component.getServiceSelectionForm().resetForms();
    }

    @Override
    public void disposeUIResources() {
        if (parentDisposable != null) {
            Disposer.dispose(parentDisposable);
        }
        component = null;
    }

    private void resetActiveTab() {
        ConversationsState.getInstance().setCurrentConversation(null);
        var project = ApplicationUtil.findCurrentProject();
        if (project == null) {
            throw new RuntimeException("Could not find current project.");
        }

        project.getService(StandardChatToolWindowContentManager.class).resetAll();
    }
}
