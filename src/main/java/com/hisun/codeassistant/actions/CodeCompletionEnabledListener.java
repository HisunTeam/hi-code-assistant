package com.hisun.codeassistant.actions;

import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import com.intellij.util.messages.Topic;

import java.util.EventListener;

/**
 * {@link EventListener} for changes of {@link ConfigurationState#isCodeCompletionsEnabled()}.
 *
 * @see EnableCompletionsAction
 * @see DisableCompletionsAction
 */
public interface CodeCompletionEnabledListener extends EventListener {
    /**
     * Topic for subscribing to {@link ConfigurationState#isCodeCompletionsEnabled()} changes.<br/>
     * Broadcasts from Application-Level to all projects.
     */
    @Topic.AppLevel
    Topic<CodeCompletionEnabledListener> TOPIC = new Topic<>(CodeCompletionEnabledListener.class,
            Topic.BroadcastDirection.TO_DIRECT_CHILDREN);

    void onCodeCompletionsEnabledChange(boolean codeCompletionsEnabled);
}
