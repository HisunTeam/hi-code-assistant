package com.hisun.codeassistant.settings.service.self;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.hisun.codeassistant.settings.service.CodeCompletionModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;

import javax.swing.*;
import java.util.Arrays;

import static com.hisun.codeassistant.ui.UIUtil.withEmptyLeftBorder;
import static java.util.stream.Collectors.toList;

public class SelfHostedLanguageModelSettingsForm {
    private final ComboBox<SelfModelEnum> completionModelComboBox;

    private final ComboBox<CodeCompletionModel> codeCompletionModelComboBox;

    public SelfHostedLanguageModelSettingsForm(SelfHostedLanguageModelSettingsState settings) {
        completionModelComboBox = new ComboBox<>(
                new EnumComboBoxModel<>(SelfModelEnum.class));
        completionModelComboBox.setSelectedItem(
                SelfModelEnum.fromName(settings.getModel()));

        var codeCompletionComboBoxModel = new DefaultComboBoxModel<CodeCompletionModel>();
        codeCompletionComboBoxModel.addAll(Arrays.stream(CodeCompletionModel.values()).collect(toList()));
        codeCompletionModelComboBox = new ComboBox<>(codeCompletionComboBoxModel);
        codeCompletionModelComboBox.setSelectedItem(getCodeCompletionModel());
        codeCompletionModelComboBox.setEnabled(false);

        completionModelComboBox.addItemListener(e -> {
            var model = ((SelfModelEnum) e.getItem());
            switch (model) {
                case ChatGLM3_6B -> {
                    codeCompletionModelComboBox.setSelectedItem(CodeCompletionModel.ChatGLM3_6B);
                }
                case GPT_3_5_0125_16k, GPT_4_0125_128K -> {
                    codeCompletionModelComboBox.setSelectedItem(CodeCompletionModel.OPENAI);
                }

            }
        });
    }

    private CodeCompletionModel getCodeCompletionModel() {
        var model = SelfModelEnum.fromName(SelfHostedLanguageModelSettings.getCurrentState().getModel());
        switch (model) {
            case ChatGLM3_6B -> {
                return CodeCompletionModel.ChatGLM3_6B;
            }
            case GPT_4_0125_128K, GPT_3_5_0125_16k -> {
                return CodeCompletionModel.OPENAI;
            }
        }
        return null;
    }

    public JPanel getForm() {
        var configurationGrid = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(completionModelComboBox)
                        .withLabel(HiCodeAssistantBundle.get("settingsConfigurable.shared.model.label"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(codeCompletionModelComboBox)
                        .withLabel(HiCodeAssistantBundle.get("settingsConfigurable.shared.code.model.label"))
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
                .getName();
    }

    public SelfHostedLanguageModelSettingsState getCurrentState() {
        var state = new SelfHostedLanguageModelSettingsState();
        state.setModel(getModel());

        switch ((SelfModelEnum) completionModelComboBox.getModel().getSelectedItem()) {
            case ChatGLM3_6B -> state.setCodeCompletionModel(CodeCompletionModel.ChatGLM3_6B.getModel());
            case GPT_3_5_0125_16k, GPT_4_0125_128K ->
                    state.setCodeCompletionModel(CodeCompletionModel.OPENAI.getModel());
        }
        return state;
    }

    public void resetForm() {
        var state = SelfHostedLanguageModelSettings.getCurrentState();
        completionModelComboBox.setSelectedItem(
                SelfModelEnum.fromName(state.getModel()));
    }
}
