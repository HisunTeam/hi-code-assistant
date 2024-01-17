package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.actions.editor.popupmenu.PopupMenuEditorActionGroupUtil;
import com.hisun.codeassistant.settings.state.HiCodeAssistantSettingsState;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HiCodeAssistantSettingsConfigurable implements Configurable, Disposable {

    private HiCodeAssistantSettingsComponent settingsComponent;
    @Override
    public void dispose() {

    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "HiCodeAssistant设置";
    }

    @Override
    public @Nullable JComponent createComponent() {
        var settings = HiCodeAssistantSettingsState.getInstance();
        settingsComponent = new HiCodeAssistantSettingsComponent(settings);
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        var settings = HiCodeAssistantSettingsState.getInstance();
        var serviceForm = settingsComponent.getHiCodeAssistantConfigForm();
        var selectedModelName = serviceForm.getSelectedModelName().getName();

        return !settingsComponent.getFullName().equals(settings.getFullName())
                || !serviceForm.getModelBaseHost().equals(settings.getModeBaselHost(selectedModelName))
                || !selectedModelName.equals(settings.getSelectedModel());
    }

    @Override
    public void apply() {
        var settings = HiCodeAssistantSettingsState.getInstance();
        settings.setFullName(settingsComponent.getFullName());

        PopupMenuEditorActionGroupUtil.refreshActions();

        var serviceForm = settingsComponent.getHiCodeAssistantConfigForm();

        settings.setModelBaseHost(serviceForm.getSelectedModelName().getName(), serviceForm.getModelBaseHost());
        settings.setSelectedModel(serviceForm.getSelectedModelName().getName());
    }
}
