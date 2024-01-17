package com.hisun.codeassistant.toolwindows.chat.editor.actions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.ActionType;
import com.hisun.codeassistant.actions.TrackableAction;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;

public class CopyAction extends TrackableAction {
    public CopyAction(@NotNull Editor editor) {
        super(
                editor,
                HiCodeAssistantBundle.get("toolwindow.chat.editor.action.copy.title"),
                HiCodeAssistantBundle.get("toolwindow.chat.editor.action.copy.description"),
                AllIcons.Actions.Copy,
                ActionType.COPY_CODE);
    }

    @Override
    public void handleAction(@NotNull AnActionEvent event) {
        StringSelection stringSelection = new StringSelection(editor.getDocument().getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
        locationOnScreen.y = locationOnScreen.y - 16;

        OverlayUtil.showInfoBalloon(
                HiCodeAssistantBundle.get("toolwindow.chat.editor.action.copy.success"),
                locationOnScreen);
    }
}
