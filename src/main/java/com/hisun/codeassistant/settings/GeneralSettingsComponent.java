package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.settings.service.ServiceSelectionForm;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

import static com.hisun.codeassistant.settings.service.ServiceType.OPENAI;
import static com.hisun.codeassistant.settings.service.ServiceType.SELF_HOSTED;
import static java.util.stream.Collectors.toList;

public class GeneralSettingsComponent {
    private final JPanel mainPanel;
    private final JBTextField displayNameField;
    private final ComboBox<ServiceType> serviceComboBox;
    private final JBCheckBox autoCompletionCheckBox;
    private final ServiceSelectionForm serviceSelectionForm;

    public GeneralSettingsComponent(GeneralSettings settings) {
        displayNameField = new JBTextField(settings.getState().getDisplayName(), 20);
        serviceSelectionForm = new ServiceSelectionForm();
        var cardLayout = new DynamicCardLayout();
        var cards = new JPanel(cardLayout);
        cards.add(serviceSelectionForm.getSelfHostedLanguageModelSettingsForm().getForm(), SELF_HOSTED.getCode());
        cards.add(serviceSelectionForm.getOpenAISettingsForm().getForm(), OPENAI.getCode());
        var serviceComboBoxModel = new DefaultComboBoxModel<ServiceType>();
        serviceComboBoxModel.addAll(Arrays.stream(ServiceType.values()).collect(toList()));
        serviceComboBox = new ComboBox<>(serviceComboBoxModel);
        serviceComboBox.setSelectedItem(SELF_HOSTED);
        serviceComboBox.setPreferredSize(displayNameField.getPreferredSize());


        autoCompletionCheckBox = new JBCheckBox(
                HiCodeAssistantBundle.get("configurationConfigurable.autoCompletion.label"),
                settings.getState().isCodeCompletionsEnabled());

        serviceComboBox.addItemListener(e -> {
            cardLayout.show(cards, ((ServiceType) e.getItem()).getCode());
        });

        autoCompletionCheckBox.addItemListener(e -> {
            boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;
            GeneralSettings.getCurrentState().setCodeCompletionsEnabled(isSelected);
        });

        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(HiCodeAssistantBundle.get("settingsConfigurable.displayName.label"), displayNameField)
                .addLabeledComponent(HiCodeAssistantBundle.get("settingsConfigurable.service.label"), serviceComboBox)
                .addComponent(autoCompletionCheckBox)
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

    public ServiceSelectionForm getServiceSelectionForm() {
        return serviceSelectionForm;
    }

    public String getDisplayName() {
        return displayNameField.getText();
    }

    public void setDisplayName(String displayName) {
        displayNameField.setText(displayName);
    }

    static class DynamicCardLayout extends CardLayout {

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Component current = findVisibleComponent(parent);
            if (current != null) {
                Insets insets = parent.getInsets();
                Dimension preferredSize = current.getPreferredSize();
                preferredSize.width += insets.left + insets.right;
                preferredSize.height += insets.top + insets.bottom;
                return preferredSize;
            }
            return super.preferredLayoutSize(parent);
        }

        private Component findVisibleComponent(Container parent) {
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    return comp;
                }
            }
            return null;
        }
    }
}
