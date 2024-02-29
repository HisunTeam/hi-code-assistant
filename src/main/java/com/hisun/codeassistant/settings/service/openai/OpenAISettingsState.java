package com.hisun.codeassistant.settings.service.openai;

import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.settings.service.CodeCompletionModel;
import lombok.Data;

import java.util.Objects;

@Data
public class OpenAISettingsState {
    private String organization = "";
    private String model = OpenAIChatCompletionModel.GPT_3_5_0125_16k.getCode();
    private String codeCompletionModel = CodeCompletionModel.OPENAI.getModel();
}
