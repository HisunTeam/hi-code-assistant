package com.hisun.codeassistant.toolwindows.chat.editor.actions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.JBMenuItem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditAction extends AbstractAction {
    private final EditorEx editor;

    public EditAction(@NotNull EditorEx editor) {
        super(HiCodeAssistantBundle.get("toolwindow.chat.editor.action.edit.title"), AllIcons.Actions.EditSource);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(@NotNull ActionEvent event) {
        editor.setViewer(!editor.isViewer());

        var viewer = editor.isViewer();
        editor.setCaretVisible(!viewer);
        editor.setCaretEnabled(!viewer);

        var settings = editor.getSettings();
        settings.setCaretRowShown(!viewer);

        var menuItem = (JBMenuItem) event.getSource();
        menuItem.setText(viewer
                ? HiCodeAssistantBundle.get("toolwindow.chat.editor.action.edit.title")
                : HiCodeAssistantBundle.get("toolwindow.chat.editor.action.disableEditing.title"));
        menuItem.setIcon(viewer ? AllIcons.Actions.EditSource : AllIcons.Diff.Lock);
    }
}
