package com.hisun.codeassistant.ui;

import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.enums.ModelEnum;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;

import javax.swing.*;
import java.util.NoSuchElementException;

public class ModelIconLabel extends JBLabel {
    public ModelIconLabel(String clientCode, String modelCode) {
        if ("self.chat.completion".equals(clientCode)) {
            setIcon(HiCodeAssistantIcons.SYSTEM_ICON);
            setText(formatSelfModelName(modelCode));
        }

        if ("chat.completion".equals(clientCode)) {
            setIcon(HiCodeAssistantIcons.OPENAI_ICON);
            setText(formatOpenAIModelName(modelCode));
        }

        setFont(JBFont.small());
        setHorizontalAlignment(SwingConstants.LEADING);
    }

    private String formatOpenAIModelName(String modelCode) {
        try {
            return OpenAIChatCompletionModel.findByCode(modelCode).getDescription();
        } catch (NoSuchElementException e) {
            return modelCode;
        }
    }

    private String formatSelfModelName(String modelCode) {
        try {
            return ModelEnum.fromName(modelCode).getDisplayName();
        } catch (NoSuchElementException e) {
            return modelCode;
        }
    }
}
