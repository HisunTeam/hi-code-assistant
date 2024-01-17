package com.hisun.codeassistant.toolwindows.components.listener;

import com.intellij.openapi.editor.Editor;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CopyActionListener implements ActionListener {
    private final Editor editor;

    public CopyActionListener(Editor editor) {
        this.editor = editor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String generatedText = this.editor.getSelectionModel().hasSelection() ?
                this.editor.getSelectionModel().getSelectedText() : this.editor.getDocument().getText();
        systemClipboard.setContents(new StringSelection(generatedText), null);
    }
}
