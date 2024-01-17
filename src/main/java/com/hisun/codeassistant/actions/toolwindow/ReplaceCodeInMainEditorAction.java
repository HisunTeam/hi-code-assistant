package com.hisun.codeassistant.actions.toolwindow;

import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import com.hisun.codeassistant.utils.EditorUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class ReplaceCodeInMainEditorAction extends AnAction {
    public ReplaceCodeInMainEditorAction() {
        super("Replace in Main Editor", "Replace code in main editor", AllIcons.Actions.Replace);
        EditorActionsUtil.registerOrReplaceAction(this);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(
                EditorUtil.isMainEditorTextSelected(requireNonNull(event.getProject()))
                        && EditorUtil.hasSelection(event.getData(PlatformDataKeys.EDITOR)));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var project = event.getProject();
        var toolWindowEditor = event.getData(PlatformDataKeys.EDITOR);
        if (project != null && toolWindowEditor != null) {
            EditorUtil.replaceMainEditorSelection(
                    project,
                    requireNonNull(toolWindowEditor.getSelectionModel().getSelectedText()));
        }
    }
}
