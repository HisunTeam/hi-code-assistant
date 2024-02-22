package com.hisun.codeassistant.toolwindows.chat.editor.actions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.actions.ActionType;
import com.hisun.codeassistant.actions.TrackableAction;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.hisun.codeassistant.utils.EditorUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class ReplaceSelectionAction extends TrackableAction {
    public ReplaceSelectionAction(@NotNull Editor editor) {
        super(
                editor,
                HiCodeAssistantBundle.get("toolwindow.chat.editor.action.replaceSelection.title"),
                HiCodeAssistantBundle.get("toolwindow.chat.editor.action.replaceSelection.description"),
//                llIcons.Actions.Replace,
                HiCodeAssistantIcons.REPLACE_ICON,
                ActionType.REPLACE_IN_MAIN_EDITOR);
    }

    @Override
    public void handleAction(@NotNull AnActionEvent event) {
        var project = requireNonNull(event.getProject());
        if (EditorUtil.isMainEditorTextSelected(project)) {
            EditorUtil.replaceMainEditorSelection(project, editor.getDocument().getText());
        } else {
            OverlayUtil.showSelectedEditorSelectionWarning(event);
        }
    }
}
