package com.hisun.codeassistant.llms.completion;

import java.util.List;

public interface SerpCompletionEventListener extends CompletionEventListener<String> {
    default void onSerpResults(List<SerpResult> results) {
    }
}
