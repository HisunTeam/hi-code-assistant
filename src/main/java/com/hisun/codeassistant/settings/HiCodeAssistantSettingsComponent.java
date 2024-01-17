package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.settings.state.HiCodeAssistantSettingsState;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import lombok.Getter;

import javax.swing.*;

public class HiCodeAssistantSettingsComponent {
    private final JPanel mainPanel;

    private final JBTextField fullNameField;

    @Getter
    private final HiCodeAssistantConfigForm hiCodeAssistantConfigForm;

    public HiCodeAssistantSettingsComponent(HiCodeAssistantSettingsState settings) {
        hiCodeAssistantConfigForm = new HiCodeAssistantConfigForm();

        fullNameField = new JBTextField(settings.getFullName(), 20);

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(UI.PanelFactory.panel(fullNameField)
                        .withLabel("用户名")
                        .resizeX(false)
                        .createPanel())
                .addComponent(new TitledSeparator("服务配置"))
                .addComponent(hiCodeAssistantConfigForm.getForm())
                .addVerticalGap(8)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    // Getting the full name from the settings
    public String getFullName() {
        return fullNameField.getText();
    }
}
