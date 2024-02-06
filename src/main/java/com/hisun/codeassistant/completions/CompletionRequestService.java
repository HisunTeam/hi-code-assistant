package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SelfHostedLanguageModelSettingsState;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Service
public final class CompletionRequestService {
    private CompletionRequestService() {
    }

    public static CompletionRequestService getInstance() {
        return ApplicationManager.getApplication().getService(CompletionRequestService.class);
    }

    public EventSource getChatCompletionAsync(CallParameters callParameters, CompletionEventListener eventListener) {
        var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
        return switch (SettingsState.getInstance().getSelectedService()) {
            case OPENAI -> {
                var openAISettings = OpenAISettingsState.getInstance();
                yield CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
                        requestProvider.buildOpenAIChatCompletionRequest(
                                openAISettings.getModel(),
                                callParameters),
                        eventListener);
            }
            case SELF_HOSTED -> {
                var selfHostedSettings = SelfHostedLanguageModelSettingsState.getInstance();
                yield CompletionClientProvider.getSelfHostedLanguageModelClient().getChatCompletionAsync(
                        requestProvider.buildSelfChatCompletionRequest(
                                selfHostedSettings.getModel(),
                                callParameters),
                        eventListener);
            }
        };
    }
    public boolean isRequestAllowed() {
        var selectedService = SettingsState.getInstance().getSelectedService();
        if (selectedService == ServiceType.SELF_HOSTED) {
            return StringUtils.isBlank(SelfHostedLanguageModelSettingsState.getInstance().getBaseHost());
        }
        if (selectedService == ServiceType.OPENAI) {
            return OpenAICredentialsManager.getInstance().isApiKeySet();
        }
        return true;
    }
}
