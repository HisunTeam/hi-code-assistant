package com.hisun.codeassistant.toolwindows.components.code;

import com.hisun.codeassistant.enums.EditorActionEnum;
import com.hisun.codeassistant.toolwindows.components.RoundedPanel;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.toolwindows.components.listener.CopyActionListener;
import com.hisun.codeassistant.toolwindows.components.listener.InsertAtCaretActionListener;
import com.hisun.codeassistant.toolwindows.components.listener.NewFileActionListener;
import com.hisun.codeassistant.toolwindows.components.listener.ReplaceSelectionActionListener;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class CodeHeaderComponent extends JPanel {
    private final Editor codeEditor;

    private final Project project;

    private final String fileExtension;

    private final EditorActionEnum editorActionEnum;

    private final Editor chosenEditor;

    public CodeHeaderComponent(
            String language,
            Editor editor,
            Project project,
            String fileExtension,
            EditorActionEnum editorActionEnum,
            Editor chosenEditor
    ) {
        super(new BorderLayout());
        this.codeEditor = editor;
        this.project = project;
        this.fileExtension = fileExtension;
        this.editorActionEnum = editorActionEnum;
        this.chosenEditor = chosenEditor;
        setBorder(JBUI.Borders.compound(JBUI.Borders.customLine(JBColor.border(), 1, 1, 1, 1),
                JBUI.Borders.empty(5)));
        add(new JBLabel(language), BorderLayout.LINE_START);
        add(createCodeActionsGroup(), BorderLayout.LINE_END);
    }

    private JPanel createCodeActionsGroup() {
        JPanel codeActionsGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));

        codeActionsGroup.add(new RoundedPanel().addIconJButton(new IconJButton(HiCodeAssistantIcons.COPY_ICON, "复制", new CopyActionListener(codeEditor))));
        codeActionsGroup.add(new RoundedPanel().addIconJButton(new IconJButton(HiCodeAssistantIcons.INSERT_AT_CARET_ICON, "在光标处插入", new InsertAtCaretActionListener(codeEditor, project))));
        codeActionsGroup.add(new RoundedPanel().addIconJButton(new IconJButton(HiCodeAssistantIcons.REPLACE_ICON, "替换选中片段", new ReplaceSelectionActionListener(codeEditor, project))));
        codeActionsGroup.add(new RoundedPanel().addIconJButton(new IconJButton(HiCodeAssistantIcons.NEW_FILE_ICON, "新建文件", new NewFileActionListener(codeEditor, fileExtension, project, editorActionEnum, chosenEditor))));
        return codeActionsGroup;
    }

}
