package com.hisun.codeassistant.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "HiCodeAssistant_IncludedFilesSettings", storages = @Storage("HiCodeAssistant_IncludedFilesSettings.xml"))
public class IncludedFilesSettingsState implements PersistentStateComponent<IncludedFilesSettingsState> {
    public static final String DEFAULT_PROMPT_TEMPLATE =
            "Use the following context to answer question at the end:\n\n"
                    + "{REPEATABLE_CONTEXT}\n\n"
                    + "Question: {QUESTION}";
    public static final String DEFAULT_REPEATABLE_CONTEXT =
            "File Path: {FILE_PATH}\n"
                    + "File Content:\n"
                    + "{FILE_CONTENT}";

    private String promptTemplate = DEFAULT_PROMPT_TEMPLATE;
    private String repeatableContext = DEFAULT_REPEATABLE_CONTEXT;

    public static IncludedFilesSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(IncludedFilesSettingsState.class);
    }

    @Override
    public @Nullable IncludedFilesSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull IncludedFilesSettingsState includedFilesSettingsState) {
        XmlSerializerUtil.copyBean(includedFilesSettingsState, this);
    }

    public String getPromptTemplate() {
        return promptTemplate;
    }

    public void setPromptTemplate(String promptTemplate) {
        this.promptTemplate = promptTemplate;
    }

    public String getRepeatableContext() {
        return repeatableContext;
    }

    public void setRepeatableContext(String repeatableContext) {
        this.repeatableContext = repeatableContext;
    }
}
