package com.hisun.codeassistant.actions;

import com.hisun.codeassistant.codecompletions.HiCodeAssistantEditorManager;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.hisun.codeassistant.settings.configuration.ConfigurationSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.hisun.codeassistant.settings.service.ServiceType.OPENAI;
import static com.hisun.codeassistant.settings.service.ServiceType.SELF_HOSTED;

public class DisableCompletionsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ConfigurationSettings.getCurrentState().setCodeCompletionsEnabled(false);
        HiCodeAssistantEditorManager.getInstance().disposeAllInlays(e.getProject());
        ApplicationManager.getApplication()
                .getMessageBus().syncPublisher(CodeCompletionEnabledListener.TOPIC)
                .onCodeCompletionsEnabledChange(false);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var selectedService = GeneralSettings.getCurrentState().getSelectedService();
        var codeCompletionEnabled = ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled();
        e.getPresentation().setEnabled(codeCompletionEnabled);
        e.getPresentation().setVisible(codeCompletionEnabled && List.of(OPENAI, SELF_HOSTED).contains(selectedService));
    }
}
