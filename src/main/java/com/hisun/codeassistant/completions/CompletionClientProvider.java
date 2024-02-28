package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.credentials.OpenAICredentialsManager;
import com.hisun.codeassistant.llms.client.openai.OpenAIClient;
import com.hisun.codeassistant.llms.client.self.SelfClient;
import com.hisun.codeassistant.settings.service.openai.OpenAISettings;
import com.hisun.codeassistant.settings.service.self.SelfHostedLanguageModelSettings;

public class CompletionClientProvider {
    public static OpenAIClient getOpenAIClient() {
        return OpenAIClient.builder()
                .apiKey(OpenAICredentialsManager.getInstance().getApiKey())
                .organization(OpenAISettings.getCurrentState().getOrganization())
                .build();
    }

    public static SelfClient getSelfHostedLanguageModelClient() {
        var baseHost =  SelfHostedLanguageModelSettings.getCurrentState().getBaseHost();
        var builder = SelfClient.builder();
        // FIXME required
        if (baseHost != null) {
            builder.host(baseHost);
        }
        return builder.build();
    }
}
