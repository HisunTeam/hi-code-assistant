package com.hisun.codeassistant.settings;

import com.hisun.codeassistant.enums.ModelEnum;
import com.hisun.codeassistant.settings.state.HiCodeAssistantSettingsState;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;

import javax.swing.*;

public class HiCodeAssistantConfigForm {
    private final JPanel servicePanel;
    private final JBTextField modelBaseHostField;
    private final ComboBox<ModelEnum> modelComboBox;

    public HiCodeAssistantConfigForm() {
        var settings = HiCodeAssistantSettingsState.getInstance();
        var selectedModel = settings.getSelectedModel();
        var selectModelEnum = ModelEnum.fromName(selectedModel);
        var host = settings.getModeBaselHost(selectedModel);
        modelBaseHostField = new JBTextField(host, 30);
        var modelTypeEnumComboBox = new ComboBox<>(ModelEnum.values());
        modelTypeEnumComboBox.setSelectedItem(selectModelEnum);
        modelTypeEnumComboBox.addItemListener(e -> {
            var selected = (ModelEnum) e.getItem();
            modelBaseHostField.setText(settings.getModeBaselHost(selected.getName()));
        });
        modelComboBox = modelTypeEnumComboBox;
        servicePanel = createServicePanel();
    }

    public JComponent getForm() {
        var form = FormBuilder.createFormBuilder()
                .addComponent(servicePanel)
                .getPanel();
        form.setBorder(JBUI.Borders.emptyLeft(16));
        return form;
    }
    private JPanel createServicePanel() {
        var panel = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(modelBaseHostField)
                        .withLabel("模型 host")
                        .resizeX(false))
                .add(UI.PanelFactory.panel(modelComboBox)
                        .withLabel("选择模型")
                        .resizeX(false))
                .createPanel();
        panel.setBorder(JBUI.Borders.emptyLeft(16));
        return panel;
    }
    public String getModelBaseHost() {
        return modelBaseHostField.getText();
    }

    public ModelEnum getSelectedModelName() {
        return (ModelEnum) modelComboBox.getSelectedItem();
    }
}
