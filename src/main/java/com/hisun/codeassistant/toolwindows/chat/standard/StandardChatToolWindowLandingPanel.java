package com.hisun.codeassistant.toolwindows.chat.standard;

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

    private ActionLink createEditorActionLink(EditorAction action, EditorActionEvent onAction) {
        return new ActionLink(action.getUserMessage(), event -> {
            onAction.handleAction(action, ((ActionLink) event.getSource()).getLocationOnScreen());
        });
    }

    private JPanel createContent(EditorActionEvent onAction) {
        var panel = new JPanel(new BorderLayout());
        panel.add(UIUtil.createTextPane(
                "<html>"
                        + format(
                        "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
                                + "Welcome <strong>%s</strong>, I'm your intelligent code companion, here to be"
                                + " your partner-in-crime for getting things done in a flash."
                                + "</p>", SettingsState.getInstance().getDisplayName())
                        + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
                        + "Feel free to ask me anything you'd like, but my true superpower lies in assisting "
                        + "you with your code! Here are a few examples of how I can assist you:"
                        + "</p>"
                        + "</html>",
                false), BorderLayout.NORTH);
        panel.add(createEditorActionsListPanel(onAction), BorderLayout.CENTER);
        panel.add(UIUtil.createTextPane(
                "<html>"
                        + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
                        + "Being an AI-powered assistant, I may occasionally have surprises or make mistakes. "
                        + "Therefore, it's wise to double-check any code or suggestions I provide."
                        + "</p>"
                        + "</html>",
                false), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEditorActionsListPanel(EditorActionEvent onAction) {
        var listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        listPanel.setBorder(JBUI.Borders.emptyLeft(4));
        listPanel.add(Box.createVerticalStrut(4));
        listPanel.add(createEditorActionLink(EditorAction.WRITE_TESTS, onAction));
        listPanel.add(Box.createVerticalStrut(4));
        listPanel.add(createEditorActionLink(EditorAction.EXPLAIN, onAction));
        listPanel.add(Box.createVerticalStrut(4));
        listPanel.add(createEditorActionLink(EditorAction.FIND_BUGS, onAction));
        listPanel.add(Box.createVerticalStrut(4));
        return listPanel;
    }
}
