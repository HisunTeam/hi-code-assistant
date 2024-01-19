package com.hisun.codeassistant.toolwindows.chat.standard;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.editor.EditorActionEnum;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.hisun.codeassistant.toolwindows.chat.ui.ResponsePanel;
import com.hisun.codeassistant.ui.UIUtil;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

import static java.lang.String.format;

public class StandardChatToolWindowLandingPanel extends ResponsePanel {
    StandardChatToolWindowLandingPanel(EditorActionEvent onAction) {
        addContent(createContent(onAction));
    }

    private ActionLink createEditorActionLink(EditorActionEnum action, EditorActionEvent onAction) {
        return new ActionLink(action.getUserMessage(), event -> {
            onAction.handleAction(action, ((ActionLink) event.getSource()).getLocationOnScreen());
        });
    }

    private JPanel createContent(EditorActionEvent onAction) {
        var panel = new JPanel(new BorderLayout());
        panel.add(UIUtil.createTextPane(
                "<html>"
                        + format(HiCodeAssistantBundle.get("toolwindow.chat.standard.welcome"), SettingsState.getInstance().getDisplayName())
                        + HiCodeAssistantBundle.get("toolwindow.chat.standard.tips")
                        + "</html>",
                false), BorderLayout.NORTH);
        panel.add(createEditorActionsListPanel(onAction), BorderLayout.CENTER);
        panel.add(UIUtil.createTextPane(
                "<html>"
                        + HiCodeAssistantBundle.get("toolwindow.chat.standard.warn")
                        + "</html>",
                false), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEditorActionsListPanel(EditorActionEvent onAction) {
        var listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        listPanel.setBorder(JBUI.Borders.emptyLeft(4));
        listPanel.add(Box.createVerticalStrut(4));
        listPanel.add(createEditorActionLink(EditorActionEnum.GENERATE_TESTS, onAction));
        listPanel.add(Box.createVerticalStrut(4));
        listPanel.add(createEditorActionLink(EditorActionEnum.EXPLAIN, onAction));
        listPanel.add(Box.createVerticalStrut(4));
        listPanel.add(createEditorActionLink(EditorActionEnum.REVIEW, onAction));
        listPanel.add(Box.createVerticalStrut(4));
        return listPanel;
    }
}
