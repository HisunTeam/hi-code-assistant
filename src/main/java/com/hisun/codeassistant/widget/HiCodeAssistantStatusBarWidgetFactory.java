package com.hisun.codeassistant.widget;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class HiCodeAssistantStatusBarWidgetFactory extends StatusBarEditorBasedWidgetFactory {
    @Override
    public @NonNls @NotNull String getId() {
        return "com.hisun.codeassistant.statusbar.widget";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return HiCodeAssistantBundle.get("project.label");
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new HiCodeAssistantStatusBarWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
    }
}
