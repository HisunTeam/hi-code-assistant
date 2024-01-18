package com.hisun.codeassistant.settings.service;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SelfHostedLanguageModelSettingsState;
import com.hisun.codeassistant.ui.UIUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import lombok.Getter;

import javax.swing.*;

public class ServiceSelectionForm {
    private final JBPasswordField openAIApiKeyField;
    private final JBTextField openAIBaseHostField;
    private final JBTextField openAIPathField;
    private final JBTextField openAIOrganizationField;
    @Getter
    private final JPanel openAIServiceSectionPanel;
    private final ComboBox<OpenAIChatCompletionModel> openAICompletionModelComboBox;

    private final JBTextField selfHostedLanguageModelBaseHostField;
    private final JBTextField selfHostedLanguageModelPathField;
    @Getter
    private final JPanel selfHostedLanguageModelServiceSectionPanel;
    private final ComboBox<SelfModelEnum> selfHostedLanguageModelComboBox;

    public ServiceSelectionForm() {
        openAIApiKeyField = new JBPasswordField();
        openAIApiKeyField.setColumns(30);
        openAIApiKeyField.setText(OpenAICredentialsManager.getInstance().getApiKey());

        var openAISettings = OpenAISettingsState.getInstance();
        openAIBaseHostField = new JBTextField(openAISettings.getBaseHost(), 30);
        openAIPathField = new JBTextField(openAISettings.getPath(), 30);
        openAIOrganizationField = new JBTextField(openAISettings.getOrganization(), 30);

        var selectedOpenAIModel = OpenAIChatCompletionModel.findByCode(openAISettings.getModel());

        openAICompletionModelComboBox = new ComboBox<>(new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
        openAICompletionModelComboBox.setSelectedItem(selectedOpenAIModel);


        var selfHostedLanguageModelSettings = SelfHostedLanguageModelSettingsState.getInstance();
        selfHostedLanguageModelBaseHostField = new JBTextField(selfHostedLanguageModelSettings.getBaseHost(), 30);
        selfHostedLanguageModelPathField = new JBTextField(selfHostedLanguageModelSettings.getPath(), 30);

        var selectedSelfHostedLanguageModel = SelfModelEnum.fromName(selfHostedLanguageModelSettings.getModel());

        selfHostedLanguageModelComboBox = new ComboBox<>(new EnumComboBoxModel<>(SelfModelEnum.class));
        selfHostedLanguageModelComboBox.setSelectedItem(selectedSelfHostedLanguageModel);


        openAIServiceSectionPanel = createOpenAIServiceSectionPanel();
        selfHostedLanguageModelServiceSectionPanel = createSelfHostedLanguageModelServiceSectionPanel();
    }

    private JPanel createOpenAIServiceSectionPanel() {
        var requestConfigurationPanel = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(openAICompletionModelComboBox)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.shared.model.label"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(openAIOrganizationField)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.service.openai.organization.label"))
                        .resizeX(false)
                        .withComment(HiCodeAssistantBundle.get(
                                "settingsConfigurable.section.openai.organization.comment")))
                .add(UI.PanelFactory.panel(openAIBaseHostField)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.shared.baseHost.label"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(openAIPathField)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.shared.path.label"))
                        .resizeX(false))
                .createPanel();

        var apiKeyFieldPanel = UI.PanelFactory.panel(openAIApiKeyField)
                .withLabel(HiCodeAssistantBundle.get("settingsConfigurable.shared.apiKey.label"))
                .resizeX(false)
                .withComment(
                        HiCodeAssistantBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
                .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
                .createPanel();

        return FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator(
                        HiCodeAssistantBundle.get("settingsConfigurable.shared.authentication.title")))
                .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
                .addComponent(new TitledSeparator(
                        HiCodeAssistantBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
                .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private JPanel createSelfHostedLanguageModelServiceSectionPanel() {
        var requestConfigurationPanel = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(selfHostedLanguageModelComboBox)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.shared.model.label"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(selfHostedLanguageModelBaseHostField)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.shared.baseHost.label"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(selfHostedLanguageModelPathField)
                        .withLabel(HiCodeAssistantBundle.get(
                                "settingsConfigurable.shared.path.label"))
                        .resizeX(false))
                .createPanel();

        return FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator(
                        HiCodeAssistantBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
                .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private JComponent withEmptyLeftBorder(JComponent component) {
        component.setBorder(JBUI.Borders.emptyLeft(16));
        return component;
    }

    public void setOpenAIApiKey(String apiKey) {
        openAIApiKeyField.setText(apiKey);
    }

    public String getOpenAIApiKey() {
        return new String(openAIApiKeyField.getPassword());
    }

    public void setOpenAIBaseHost(String baseHost) {
        openAIBaseHostField.setText(baseHost);
    }

    public String getOpenAIBaseHost() {
        return openAIBaseHostField.getText();
    }

    public void setOpenAIOrganization(String organization) {
        openAIOrganizationField.setText(organization);
    }

    public String getOpenAIOrganization() {
        return openAIOrganizationField.getText();
    }

    public void setOpenAIModel(String model) {
        openAICompletionModelComboBox.setSelectedItem(OpenAIChatCompletionModel.findByCode(model));
    }

    public String getOpenAIModel() {
        return ((OpenAIChatCompletionModel) (openAICompletionModelComboBox.getModel()
                .getSelectedItem()))
                .getCode();
    }

    public void setOpenAIPath(String path) {
        openAIPathField.setText(path);
    }

    public String getOpenAIPath() {
        return openAIPathField.getText();
    }


    public void setSelfHostedLanguageModelBaseHost(String baseHost) {
        selfHostedLanguageModelBaseHostField.setText(baseHost);
    }

    public String getSelfHostedLanguageModelBaseHost() {
        return selfHostedLanguageModelBaseHostField.getText();
    }

    public void setSelfHostedLanguageModel(String model) {
        selfHostedLanguageModelComboBox.setSelectedItem(SelfModelEnum.fromName(model));
    }

    public String getSelfHostedLanguageModel() {
        return ((SelfModelEnum) (selfHostedLanguageModelComboBox.getModel()
                .getSelectedItem()))
                .getName();
    }

    public void setSelfHostedLanguageModelPath(String path) {
        selfHostedLanguageModelPathField.setText(path);
    }

    public String getSelfHostedLanguageModelPath() {
        return selfHostedLanguageModelPathField.getText();
    }

}
