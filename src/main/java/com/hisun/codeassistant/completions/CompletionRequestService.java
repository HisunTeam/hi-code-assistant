package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.codecompletions.CodeCompletionRequestProvider;
import com.hisun.codeassistant.codecompletions.InfillRequestDetails;
import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.service.openai.OpenAISettings;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;

@Service
public final class CompletionRequestService {
    private CompletionRequestService() {
    }

    public static CompletionRequestService getInstance() {
        return ApplicationManager.getApplication().getService(CompletionRequestService.class);
    }

    public EventSource getChatCompletionAsync(CallParameters callParameters, CompletionEventListener eventListener) {
        var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
        return switch (GeneralSettings.getCurrentState().getSelectedService()) {
            case OPENAI -> CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
                    requestProvider.buildOpenAIChatCompletionRequest(
                            OpenAISettings.getCurrentState().getModel(),
                            callParameters),
                    eventListener);
            case SELF_HOSTED -> CompletionClientProvider.getSelfHostedLanguageModelClient().getChatCompletionAsync(
                    requestProvider.buildSelfChatCompletionRequest(
                            SelfHostedLanguageModelSettings.getCurrentState().getModel(),
                            callParameters),
                    eventListener);
        };
    }

    public EventSource getCodeCompletionAsync(
            InfillRequestDetails requestDetails,
            CompletionEventListener<String> eventListener) {
        var requestProvider = new CodeCompletionRequestProvider(requestDetails);
        return switch (GeneralSettings.getCurrentState().getSelectedService()) {
            case OPENAI -> CompletionClientProvider.getOpenAIClient()
                    .getCompletionAsync(requestProvider.buildOpenAIRequest(), eventListener);
            case SELF_HOSTED -> CompletionClientProvider.getSelfHostedLanguageModelClient()
                    .getCompletionAsync(requestProvider.buildSelfRequest(), eventListener);
        };
    }

    public boolean isRequestAllowed() {
        var selectedService = GeneralSettings.getCurrentState().getSelectedService();
        if (selectedService == ServiceType.SELF_HOSTED) {
            return StringUtils.isNotBlank(SelfHostedLanguageModelSettings.getCurrentState().getBaseHost());
        }
        if (selectedService == ServiceType.OPENAI) {
            return OpenAICredentialsManager.getInstance().isApiKeySet();
        }
        return true;
    }
}
