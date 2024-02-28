package com.hisun.codeassistant;

import com.hisun.codeassistant.embedding.ReferencedFile;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.util.Key;

import java.util.List;

public class HiCodeAssistantKeys {
    public static final Key<List<ReferencedFile>> SELECTED_FILES = Key.create("HiCodeAssistant.selectedFiles");

    public static final Key<Inlay<EditorCustomElementRenderer>> SINGLE_LINE_INLAY = Key.create("HiCodeAssistant.editor.inlay.single-line");
    public static final Key<Inlay<EditorCustomElementRenderer>> MULTI_LINE_INLAY = Key.create("HiCodeAssistant.editor.inlay.multi-line");

    public static final Key<String> PREVIOUS_INLAY_TEXT = Key.create("HiCodeAssistant.editor.inlay.prev-value");
}
