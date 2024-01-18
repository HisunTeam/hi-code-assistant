package com.hisun.codeassistant;

import com.hisun.codeassistant.embedding.ReferencedFile;
import com.intellij.openapi.util.Key;

import java.util.List;

public class HiCodeAssistantKeys {
    public static final Key<List<ReferencedFile>> SELECTED_FILES = Key.create("selectedFiles");
}
