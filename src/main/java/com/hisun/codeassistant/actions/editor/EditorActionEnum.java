package com.hisun.codeassistant.actions.editor;

import com.hisun.codeassistant.HiCodeAssistantBundle;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

import static com.hisun.codeassistant.utils.file.FileUtil.getResourceContent;

public enum EditorActionEnum {
    REVIEW(HiCodeAssistantBundle.get("action.code.review"), HiCodeAssistantBundle.get("action.code.review.message"), getResourceContent("/prompts/review.txt"), KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_DOWN_MASK)),
    REFACTOR(HiCodeAssistantBundle.get("action.code.refactor"), HiCodeAssistantBundle.get("action.code.refactor.message"), getResourceContent("/prompts/refactor.txt"), null),
    PERFORMANCE(HiCodeAssistantBundle.get("action.code.performance"), HiCodeAssistantBundle.get("action.code.performance.message"), getResourceContent("/prompts/performance.txt"), null),
    SECURITY(HiCodeAssistantBundle.get("action.code.security"), HiCodeAssistantBundle.get("action.code.security.message"), getResourceContent("/prompts/security.txt"), null),
    OPTIMIZE(HiCodeAssistantBundle.get("action.code.optimize"), HiCodeAssistantBundle.get("action.code.optimize.message"), getResourceContent("/prompts/optimize.txt"), null),
    COMMENT(HiCodeAssistantBundle.get("action.code.comment"), HiCodeAssistantBundle.get("action.code.comment.message"), getResourceContent("/prompts/comment.txt"), null),
    EXPLAIN(HiCodeAssistantBundle.get("action.code.explain"), HiCodeAssistantBundle.get("action.code.explain.message"), getResourceContent("/prompts/explain.txt"), KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_DOWN_MASK)),
    GENERATE_TESTS(HiCodeAssistantBundle.get("action.code.test"), HiCodeAssistantBundle.get("action.code.test.message"), getResourceContent("/prompts/test.txt"), KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK));

    private final String label;

    private final String userMessage;

    private final String prompt;
    private final KeyStroke keyStroke;

    EditorActionEnum(String label, String userMessage, String prompt, KeyStroke keyStroke) {
        this.label = label;
        this.userMessage = userMessage;
        this.prompt = prompt;
        this.keyStroke = keyStroke;
    }

    public static EditorActionEnum getEnumByLabel(String label) {
        if (Objects.isNull(label)) {
            return null;
        }
        for (EditorActionEnum type : EditorActionEnum.values()) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getUserMessage() {
        return userMessage;
    }
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}
