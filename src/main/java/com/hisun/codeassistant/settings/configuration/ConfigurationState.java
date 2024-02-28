package com.hisun.codeassistant.settings.configuration;

import com.hisun.codeassistant.actions.editor.EditorActionEnum;
import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import lombok.Data;

import java.util.ArrayList;

import static com.hisun.codeassistant.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;

@Data
public class ConfigurationState {
    private String systemPrompt = COMPLETION_SYSTEM_PROMPT;
    private int maxTokens = 1000;
    private double temperature = 0.1;
    private boolean autoFormattingEnabled = true;

    private boolean codeCompletionsEnabled;
    private ArrayList<EditorActionEnum> tableData = EditorActionsUtil.DEFAULT_ACTIONS;
}
