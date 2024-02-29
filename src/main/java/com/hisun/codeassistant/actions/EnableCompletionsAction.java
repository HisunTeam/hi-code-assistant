package com.hisun.codeassistant.actions;

import com.hisun.codeassistant.settings.GeneralSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

public class EnableCompletionsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        GeneralSettings.getCurrentState().setCodeCompletionsEnabled(true);
        ApplicationManager.getApplication()
                .getMessageBus().syncPublisher(CodeCompletionEnabledListener.TOPIC)
                .onCodeCompletionsEnabledChange(true);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var codeCompletionEnabled = GeneralSettings.getCurrentState().isCodeCompletionsEnabled();
        e.getPresentation().setEnabled(!codeCompletionEnabled);
        e.getPresentation().setVisible(!codeCompletionEnabled);
    }
}
