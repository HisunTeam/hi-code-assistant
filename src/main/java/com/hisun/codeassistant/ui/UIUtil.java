package com.hisun.codeassistant.ui;

import com.hisun.codeassistant.toolwindows.chat.ui.SmartScroller;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.net.URISyntaxException;

import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

public class UIUtil {
    public static JTextPane createTextPane(String text) {
        return createTextPane(text, true);
    }

    public static JScrollPane createScrollPaneWithSmartScroller(ScrollablePanel scrollablePanel) {
        var scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        new SmartScroller(scrollPane);
        return scrollPane;
    }

    public static JTextPane createTextPane(String text, boolean opaque) {
        return createTextPane(text, opaque, UIUtil::handleHyperlinkClicked);
    }

    public static JTextPane createTextPane(String text, boolean opaque, HyperlinkListener listener) {
        var textPane = new JTextPane();
        textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
        textPane.addHyperlinkListener(listener);
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setText(text);
        textPane.setOpaque(opaque);
        return textPane;
    }
    public static void handleHyperlinkClicked(HyperlinkEvent event) {
        var url = event.getURL();
        if (ACTIVATED.equals(event.getEventType()) && url != null) {
            try {
                BrowserUtil.browse(url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addShiftEnterInputMap(JTextArea textArea, AbstractAction onSubmit) {
        textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "text-submit");
        textArea.getActionMap().put("text-submit", onSubmit);
    }

    public static JButton createIconButton(Icon icon) {
        var button = new JButton(icon);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        return button;
    }

    public static <T extends JComponent> T withEmptyLeftBorder(T component) {
        component.setBorder(JBUI.Borders.emptyLeft(16));
        return component;
    }
}
