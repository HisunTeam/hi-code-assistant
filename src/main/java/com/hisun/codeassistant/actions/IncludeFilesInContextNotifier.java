package com.hisun.codeassistant.actions;

import com.hisun.codeassistant.embedding.ReferencedFile;
import com.intellij.util.messages.Topic;

import java.util.List;

public interface IncludeFilesInContextNotifier {
    Topic<IncludeFilesInContextNotifier> FILES_INCLUDED_IN_CONTEXT_TOPIC =
            Topic.create("filesIncludedInContext", IncludeFilesInContextNotifier.class);

    void filesIncluded(List<ReferencedFile> includedFiles);
}
