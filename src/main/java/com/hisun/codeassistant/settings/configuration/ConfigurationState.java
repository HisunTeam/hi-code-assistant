package com.hisun.codeassistant.settings.configuration;

import com.hisun.codeassistant.actions.editor.EditorActionPair;
import com.hisun.codeassistant.actions.editor.EditorActionsUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hisun.codeassistant.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;

@Data
@State(name = "HiCodeAssistant_ConfigurationSettings_0206", storages = @Storage("HiCodeAssistant_ConfigurationSettings_0206.xml"))
public class ConfigurationState implements PersistentStateComponent<ConfigurationState> {
    private String systemPrompt = COMPLETION_SYSTEM_PROMPT;
    private int maxTokens = 1000;
    private double temperature = 0.1;
    private boolean autoFormattingEnabled = true;
    private ArrayList<EditorActionPair> tableData = EditorActionsUtil.DEFAULT_ACTIONS;

    public static ConfigurationState getInstance() {
        return ApplicationManager.getApplication().getService(ConfigurationState.class);
    }

    @Override
    public @Nullable ConfigurationState getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull ConfigurationState configurationState) {

    }
}
