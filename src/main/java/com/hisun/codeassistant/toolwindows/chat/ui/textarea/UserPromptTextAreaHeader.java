package com.hisun.codeassistant.toolwindows.chat.ui.textarea;

import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.toolwindows.chat.standard.ModelComboBoxAction;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class UserPromptTextAreaHeader extends JPanel {
    public UserPromptTextAreaHeader(
            ServiceType selectedService,
            TotalTokensPanel totalTokensPanel,
            Runnable onAddNewTab) {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(JBUI.Borders.emptyBottom(8));
        switch (selectedService) {
            case OPENAI:
            case SELF_HOSTED:
                add(totalTokensPanel, BorderLayout.LINE_START);
                break;
            default:
        }
        add(new ModelComboBoxAction(onAddNewTab, selectedService).createCustomComponent(ActionPlaces.UNKNOWN), BorderLayout.LINE_END);
    }
}
