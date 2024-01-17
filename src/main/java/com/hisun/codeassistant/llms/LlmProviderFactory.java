package com.hisun.codeassistant.llms;

import com.hisun.codeassistant.enums.ModelEnum;
import com.hisun.codeassistant.llms.client.openai.OpenAIServiceProvider;
import com.hisun.codeassistant.settings.state.HiCodeAssistantSettingsState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service
public final class LlmProviderFactory {
    public LlmProvider getLlmProvider(Project project) {
        var settings = HiCodeAssistantSettingsState.getInstance();
        String selectedModel = settings.getSelectedModel();
        ModelEnum modelEnum = ModelEnum.fromName(selectedModel);

        switch (modelEnum) {
            case ChatGLM3_6B:
                return project.getService(OpenAIServiceProvider.class);
        }

        return project.getService(OpenAIServiceProvider.class);
    }
}
