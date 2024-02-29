package com.hisun.codeassistant.actions;

import com.hisun.codeassistant.codecompletions.HiCodeAssistantEditorManager;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

public class DisableCompletionsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        GeneralSettings.getCurrentState().setCodeCompletionsEnabled(false);
        HiCodeAssistantEditorManager.getInstance().disposeAllInlays(e.getProject());
        ApplicationManager.getApplication()
                .getMessageBus().syncPublisher(CodeCompletionEnabledListener.TOPIC)
                .onCodeCompletionsEnabledChange(false);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var codeCompletionEnabled = GeneralSettings.getCurrentState().isCodeCompletionsEnabled();
        e.getPresentation().setEnabled(codeCompletionEnabled);
        e.getPresentation().setVisible(codeCompletionEnabled);
    }
}
