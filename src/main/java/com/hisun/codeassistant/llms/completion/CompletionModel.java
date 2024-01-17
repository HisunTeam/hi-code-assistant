package com.hisun.codeassistant.llms.completion;

public interface CompletionModel {
    String getCode();

    String getDescription();

    int getMaxTokens();
}
