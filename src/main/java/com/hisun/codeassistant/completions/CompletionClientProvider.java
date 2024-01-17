package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.llms.client.openai.OpenAIClient;
import com.hisun.codeassistant.llms.client.self.SelfClient;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SelfHostedLanguageModelSettingsState;

public class CompletionClientProvider {
    public static OpenAIClient getOpenAIClient() {
        var settings = OpenAISettingsState.getInstance();
        var builder = OpenAIClient.builder()
                .apiKey(OpenAICredentialsManager.getInstance().getApiKey())
                .organization(settings.getOrganization());
        var baseHost = settings.getBaseHost();
        if (baseHost != null) {
            builder.host(baseHost);
        }
        return builder.build();
    }

    public static SelfClient getSelfHostedLanguageModelClient() {
        var settings = SelfHostedLanguageModelSettingsState.getInstance();
        var builder = SelfClient.builder();
        var baseHost = settings.getBaseHost();
        // FIXME required
        if (baseHost != null) {
            builder.host(baseHost);
        }
        return builder.build();
    }
}
