package com.hisun.codeassistant.widget;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HiCodeAssistantStatusBarWidget extends EditorBasedStatusBarPopup {
    public HiCodeAssistantStatusBarWidget(Project project) {
        super(project, false);
    }

    @Override
    protected @NotNull WidgetState getWidgetState(@Nullable VirtualFile file) {
        var state = new WidgetState(HiCodeAssistantBundle.get("statusBar.widget.tooltip"), "", true);
        state.setIcon(HiCodeAssistantIcons.SYSTEM_ICON);
        return state;
    }

    @Override
    protected @Nullable ListPopup createPopup(DataContext context) {
        return JBPopupFactory.getInstance()
                .createActionGroupPopup(
                        HiCodeAssistantBundle.get("project.label"),
                        (ActionGroup) ActionManager.getInstance().getAction("HiCodeAssistant.statusBarPopup"),
                        context,
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        true);
    }

    @Override
    protected @NotNull StatusBarWidget createInstance(@NotNull Project project) {
        return new HiCodeAssistantStatusBarWidget(project);
    }

    @Override
    public @NonNls @NotNull String ID() {
        return "com.hisun.codeassistant.statusbar.widget";
    }
}
