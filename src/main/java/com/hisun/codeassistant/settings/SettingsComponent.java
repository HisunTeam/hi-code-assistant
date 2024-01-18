package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.settings.service.ServiceSelectionForm;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SettingsComponent {
    private final JPanel mainPanel;
    private final JBTextField displayNameField;
    private final ComboBox<ServiceType> serviceComboBox;
    @Getter
    private final ServiceSelectionForm serviceSelectionForm;

    public SettingsComponent(Disposable parentDisposable, SettingsState settings) {
        displayNameField = new JBTextField(settings.getDisplayName(), 20);

        serviceSelectionForm = new ServiceSelectionForm();
        var cardLayout = new CardLayout();
        var cards = new JPanel(cardLayout);
        cards.add(serviceSelectionForm.getSelfHostedLanguageModelServiceSectionPanel(), ServiceType.SELF_HOSTED.getCode());
        cards.add(serviceSelectionForm.getOpenAIServiceSectionPanel(), ServiceType.OPENAI.getCode());
        var serviceComboBoxModel = new DefaultComboBoxModel<ServiceType>();
        serviceComboBoxModel.addAll(Arrays.stream(ServiceType.values()).toList());
        serviceComboBox = new ComboBox<>(serviceComboBoxModel);
        // 需要与cards的顺序一致
        serviceComboBox.setSelectedItem(ServiceType.SELF_HOSTED);
        serviceComboBox.setPreferredSize(displayNameField.getPreferredSize());
        var serviceInputValidator = createInputValidator(parentDisposable, serviceComboBox);
        serviceInputValidator.revalidate();
        serviceComboBox.addItemListener(e -> {
            serviceInputValidator.revalidate();
            cardLayout.show(cards, ((ServiceType) e.getItem()).getCode());
        });

        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        HiCodeAssistantBundle.get("settingsConfigurable.displayName.label"),
                        displayNameField)
                .addLabeledComponent(
                        HiCodeAssistantBundle.get("settingsConfigurable.service.label"),
                        serviceComboBox)
                .addComponent(cards)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public ServiceType getSelectedService() {
        return serviceComboBox.getItem();
    }

    public void setSelectedService(ServiceType serviceType) {
        serviceComboBox.setSelectedItem(serviceType);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return displayNameField;
    }

    public String getDisplayName() {
        return displayNameField.getText();
    }

    public void setDisplayName(String displayName) {
        displayNameField.setText(displayName);
    }

    private ComponentValidator createInputValidator(
            Disposable parentDisposable,
            JComponent component) {
        var validator = new ComponentValidator(parentDisposable)
                .withValidator(() -> {
                    if (component instanceof ComboBox) {
                        var selectedItem = ((ComboBox<?>) component).getSelectedItem();
                        if (selectedItem == ServiceType.OPENAI
                                && OpenAISettingsState.getInstance().isOpenAIQuotaExceeded()) {
                            return new ValidationInfo(
                                    HiCodeAssistantBundle.get("settings.openaiQuotaExceeded"),
                                    component);
                        }
                    }

                    return null;
                })
                .andStartOnFocusLost()
                .installOn(component);
        validator.enableValidation();
        return validator;
    }
}
