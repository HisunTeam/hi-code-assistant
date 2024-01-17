package com.hisun.codeassistant.toolwindows.chat.editor.actions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.hisun.codeassistant.utils.EditorUtil;
import com.hisun.codeassistant.utils.file.FileUtil;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUtil;
import com.intellij.diff.util.Side;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static java.util.Objects.requireNonNull;

public class DiffAction extends AbstractAction {
    private final EditorEx editor;
    private final Point locationOnScreen;

    public DiffAction(@NotNull EditorEx editor, @NotNull Point locationOnScreen) {
        super("Diff", AllIcons.Actions.DiffWithClipboard);
        this.editor = editor;
        this.locationOnScreen = locationOnScreen;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        var project = requireNonNull(editor.getProject());
        var selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (!EditorUtil.hasSelection(selectedTextEditor)) {
            OverlayUtil.showSelectedEditorSelectionWarning(project, locationOnScreen);
            return;
        }

        var resultEditorFile = FileUtil.getEditorFile(selectedTextEditor);
        var diffContentFactory = DiffContentFactory.getInstance();
        var request = new SimpleDiffRequest(
                HiCodeAssistantBundle.get("editor.diff.title"),
                diffContentFactory.create(project, FileUtil.getEditorFile(editor)),
                diffContentFactory.create(project, resultEditorFile),
                HiCodeAssistantBundle.get("editor.diff.local.content.title"),
                resultEditorFile.getName());
        request.putUserData(
                DiffUserDataKeys.SCROLL_TO_LINE,
                Pair.create(Side.RIGHT, DiffUtil.getCaretPosition(selectedTextEditor).line));

        DiffManager.getInstance().showDiff(project, request, DiffDialogHints.DEFAULT);
    }
}
