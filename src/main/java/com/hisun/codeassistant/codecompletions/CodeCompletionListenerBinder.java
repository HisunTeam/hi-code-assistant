package com.hisun.codeassistant.codecompletions;

import com.hisun.codeassistant.actions.CodeCompletionEnabledListener;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static com.hisun.codeassistant.HiCodeAssistantKeys.PREVIOUS_INLAY_TEXT;

public class CodeCompletionListenerBinder implements Disposable {
    private final Editor editor;

    private @Nullable EditorDocumentListener documentListener;
    private @Nullable EditorSelectionListener selectionListener;
    private @Nullable EditorCaretListener caretListener;

    public CodeCompletionListenerBinder(Editor editor) {
        this.editor = editor;

        if (GeneralSettings.getCurrentState().isCodeCompletionsEnabled()) {
            addListeners();
        }

        ApplicationManager.getApplication()
                .getMessageBus()
                .connect(this)
                .subscribe(
                        CodeCompletionEnabledListener.TOPIC,
                        (CodeCompletionEnabledListener) (completionsEnabled) -> {
                            if (completionsEnabled) {
                                addListeners();
                            } else {
                                removeListeners();
                            }
                        });
    }

    private void addListeners() {
        if (documentListener == null) {
            documentListener = new EditorDocumentListener();
            editor.getDocument().addDocumentListener(documentListener);
        }
        if (selectionListener == null) {
            selectionListener = new EditorSelectionListener();
            editor.getSelectionModel().addSelectionListener(selectionListener);
        }
        if (caretListener == null) {
            caretListener = new EditorCaretListener();
            editor.getCaretModel().addCaretListener(caretListener);
        }
    }

    private void removeListeners() {
        if (documentListener != null) {
            editor.getDocument().removeDocumentListener(documentListener);
            documentListener = null;
        }
        if (selectionListener != null) {
            editor.getSelectionModel().removeSelectionListener(selectionListener);
            selectionListener = null;
        }
        if (caretListener != null) {
            editor.getCaretModel().removeCaretListener(caretListener);
            caretListener = null;
        }
    }

    @Override
    public void dispose() {
        removeListeners();
    }

    private class EditorSelectionListener implements SelectionListener {

        @Override
        public void selectionChanged(@NotNull SelectionEvent event) {
            HiCodeAssistantEditorManager.getInstance().disposeEditorInlays(editor);
        }
    }

    private class EditorCaretListener implements CaretListener {

        @Override
        public void caretPositionChanged(@NotNull CaretEvent event) {
            if (!"Typing".equals(CommandProcessor.getInstance().getCurrentCommandName())) {
                HiCodeAssistantEditorManager.getInstance().disposeEditorInlays(editor);
                if (editor.getProject() != null) {
                    CodeCompletionService.getInstance(editor.getProject()).cancelPreviousCall();
                }
            }
        }
    }

    private class EditorDocumentListener implements BulkAwareDocumentListener {

        @Override
        public void documentChangedNonBulk(@NotNull DocumentEvent event) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                HiCodeAssistantEditorManager.getInstance().disposeEditorInlays(editor);

                var commandName = CommandProcessor.getInstance().getCurrentCommandName();
                if (CommandProcessor.getInstance().isUndoTransparentActionInProgress()
                        || isCommandExcluded(commandName)) {
                    return;
                }

                var project = editor.getProject();
                if (project != null) {
                    var codeCompletionService = CodeCompletionService.getInstance(project);
                    SwingUtilities.invokeLater(() -> {
                        var caretOffset = editor.getCaretModel().getOffset();
                        var charTyped = event.getNewFragment().toString().trim();
                        if (isTypingAsSuggested(charTyped)) {
                            try {
                                var previousInlayText = PREVIOUS_INLAY_TEXT.get(editor).replaceFirst(charTyped, "");
                                codeCompletionService.addInlays(editor, caretOffset, previousInlayText);
                            } catch (PatternSyntaxException e) {
                                // ignore
                            }
                        } else {
                            codeCompletionService.handleCompletions(editor, caretOffset);
                        }
                    });
                }
            });
        }

        private boolean isTypingAsSuggested(String charTyped) {
            if (charTyped.isEmpty()) {
                return false;
            }

            var prevInlay = PREVIOUS_INLAY_TEXT.get(editor);
            return prevInlay != null && prevInlay.startsWith(charTyped);
        }

        private boolean isCommandExcluded(String commandName) {
            return commandName != null
                    && List.of("Up", "Down", "Left", "Right", "Move Caret").contains(commandName);
        }
    }
}
