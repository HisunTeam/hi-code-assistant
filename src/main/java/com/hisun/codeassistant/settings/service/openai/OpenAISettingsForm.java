package com.hisun.codeassistant.settings.service.openai;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.credentials.OpenAICredentialManager;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.hisun.codeassistant.settings.service.CodeCompletionModel;
import com.hisun.codeassistant.ui.UIUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;

import javax.swing.*;

import java.util.Arrays;

import static com.hisun.codeassistant.ui.UIUtil.withEmptyLeftBorder;
import static java.util.stream.Collectors.toList;

public class OpenAISettingsForm {
    private final JBPasswordField apiKeyField;
    private final JBTextField organizationField;
    private final ComboBox<OpenAIChatCompletionModel> completionModelComboBox;

    private final ComboBox<CodeCompletionModel> codeCompletionModelComboBox;

    public OpenAISettingsForm(OpenAISettingsState settings) {
        apiKeyField = new JBPasswordField();
        apiKeyField.setColumns(30);
        apiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
        organizationField = new JBTextField(settings.getOrganization(), 30);
        completionModelComboBox = new ComboBox<>(
                new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
        completionModelComboBox.setSelectedItem(
                OpenAIChatCompletionModel.findByCode(settings.getModel()));

        var codeCompletionComboBoxModel = new DefaultComboBoxModel<CodeCompletionModel>();
        codeCompletionComboBoxModel.addElement(CodeCompletionModel.OPENAI);
        codeCompletionModelComboBox = new ComboBox<>(codeCompletionComboBoxModel);
        codeCompletionModelComboBox.setSelectedItem(CodeCompletionModel.OPENAI);
        codeCompletionModelComboBox.setEnabled(false);
    }

    public JPanel getForm() {
        var configurationGrid = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(apiKeyField)
                        .withLabel(HiCodeAssistantBundle.get("settingsConfigurable.shared.apiKey.label"))
                        .resizeX(false)
                        .withComment(HiCodeAssistantBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
                        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked))
                .add(UI.PanelFactory.panel(organizationField)
                        .withLabel(HiCodeAssistantBundle.get("settingsConfigurable.service.openai.organization.label"))
                        .resizeX(false)
                        .withComment(HiCodeAssistantBundle.get(
                                "settingsConfigurable.section.openai.organization.comment")))
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

    public String getApiKey() {
        var apiKey = new String(apiKeyField.getPassword());
        return apiKey.isEmpty() ? null : apiKey;
    }

    public String getModel() {
        return ((OpenAIChatCompletionModel) (completionModelComboBox.getModel()
                .getSelectedItem()))
                .getCode();
    }

    public OpenAISettingsState getCurrentState() {
        var state = new OpenAISettingsState();
        state.setModel(getModel());
        state.setOrganization(organizationField.getText());
        return state;
    }

    public void resetForm() {
        var state = OpenAISettings.getCurrentState();
        apiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
        completionModelComboBox.setSelectedItem(
                OpenAIChatCompletionModel.findByCode(state.getModel()));
        organizationField.setText(state.getOrganization());
    }
}
