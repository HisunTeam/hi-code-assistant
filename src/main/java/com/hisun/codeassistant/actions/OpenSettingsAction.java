package com.hisun.codeassistant.actions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.settings.GeneralSettingsConfigurable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;

public class OpenSettingsAction extends AnAction {
    public OpenSettingsAction() {
        super(HiCodeAssistantBundle.get("action.openSettings.title"),
                HiCodeAssistantBundle.get("action.openSettings.description"),
                AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), GeneralSettingsConfigurable.class);
    }
}
