package com.hisun.codeassistant.settings.service.self;

import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.hisun.codeassistant.settings.service.openai.OpenAISettingsState;
import lombok.Data;

import java.util.Objects;

@Data
public class SelfHostedLanguageModelSettingsState {

    private static final String BASE_PATH = "/v1/chat/completions";
    private String baseHost = "http://10.9.50.190:8000";
    private String path = BASE_PATH;
    private String model = OpenAIChatCompletionModel.GPT_3_5_0125_16k.getCode();
}
