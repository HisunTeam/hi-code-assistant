package com.hisun.codeassistant.settings.service.self;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;

import javax.swing.*;

import static com.hisun.codeassistant.ui.UIUtil.withEmptyLeftBorder;

public class SelfHostedLanguageModelSettingsForm {
    private final ComboBox<SelfModelEnum> completionModelComboBox;

    public SelfHostedLanguageModelSettingsForm(SelfHostedLanguageModelSettingsState settings) {
        completionModelComboBox = new ComboBox<>(
                new EnumComboBoxModel<>(SelfModelEnum.class));
        completionModelComboBox.setSelectedItem(
                SelfModelEnum.fromName(settings.getModel()));
    }

    public JPanel getForm() {
        var configurationGrid = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(completionModelComboBox)
                        .withLabel(HiCodeAssistantBundle.get("settingsConfigurable.shared.model.label"))
                        .resizeX(false))
                .createPanel();

        return FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator(
                        HiCodeAssistantBundle.get("settingsConfigurable.service.openai.configuration.title")))
                .addComponent(withEmptyLeftBorder(configurationGrid))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public String getModel() {
        return ((SelfModelEnum) (completionModelComboBox.getModel()
                .getSelectedItem()))
                .getCode();
    }

    public SelfHostedLanguageModelSettingsState getCurrentState() {
        var state = new SelfHostedLanguageModelSettingsState();
        state.setModel(getModel());
        return state;
    }

    public void resetForm() {
        var state = SelfHostedLanguageModelSettings.getCurrentState();
        completionModelComboBox.setSelectedItem(
                SelfModelEnum.fromName(state.getModel()));
    }
}
