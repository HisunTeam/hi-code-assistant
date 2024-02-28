package com.hisun.codeassistant.codecompletions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.OpenSettingsAction;
import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import com.hisun.codeassistant.ui.OverlayUtil;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

import static com.hisun.codeassistant.HiCodeAssistantKeys.PREVIOUS_INLAY_TEXT;
import static java.util.Objects.requireNonNull;

public class CodeCompletionEventListener implements CompletionEventListener<String> {
    private static final Logger LOG = Logger.getInstance(CodeCompletionEventListener.class);

    private final Editor editor;
    private final int caretOffset;
    private final BackgroundableProcessIndicator progressIndicator;

    public CodeCompletionEventListener(
            Editor editor,
            int caretOffset,
            @Nullable BackgroundableProcessIndicator progressIndicator) {
        this.editor = editor;
        this.caretOffset = caretOffset;
        this.progressIndicator = progressIndicator;
    }

    @Override
    public void onComplete(StringBuilder messageBuilder) {
        if (progressIndicator != null) {
            progressIndicator.processFinish();
        }

        PREVIOUS_INLAY_TEXT.set(editor, messageBuilder.toString());
        HiCodeAssistantEditorManager.getInstance().disposeEditorInlays(editor);
        SwingUtilities.invokeLater(() -> {
            if (editor.getCaretModel().getOffset() == caretOffset) {
                var inlayText = messageBuilder.toString();
                if (!inlayText.isEmpty()) {
                    CodeCompletionService.getInstance(requireNonNull(editor.getProject()))
                            .addInlays(editor, caretOffset, inlayText);
                }
            }
        });
    }

    @Override
    public void onError(OpenAiError.OpenAiErrorDetails error, Throwable ex) {
        // TODO: temp fix
        if (ex instanceof IOException && "Canceled".equals(error.getMessage())) {
            return;
        }

        LOG.error(error.getMessage(), ex);
        if (progressIndicator != null) {
            progressIndicator.processFinish();
        }
        Notifications.Bus.notify(OverlayUtil.getDefaultNotification(
                        String.format(
                                HiCodeAssistantBundle.get("notification.completionError.description"),
                                error.getMessage()),
                        NotificationType.ERROR)
                .addAction(new OpenSettingsAction()), editor.getProject());
    }

    @Override
    public void onCancelled(StringBuilder messageBuilder) {
        LOG.debug("Completion cancelled");
        if (progressIndicator != null) {
            progressIndicator.processFinish();
        }
    }
}
