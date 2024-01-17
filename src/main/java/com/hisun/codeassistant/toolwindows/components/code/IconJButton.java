package com.hisun.codeassistant.toolwindows.components.code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class IconJButton extends JButton {
    public IconJButton(Icon icon, String tipText, ActionListener actionListener) {
        super(icon);
        setToolTipText(tipText);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        addActionListener(actionListener);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
