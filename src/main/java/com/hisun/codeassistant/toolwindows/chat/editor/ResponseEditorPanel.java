package com.hisun.codeassistant.toolwindows.chat.editor;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.actions.toolwindow.ReplaceCodeInMainEditorAction;
import com.hisun.codeassistant.toolwindows.chat.editor.actions.*;
import com.hisun.codeassistant.ui.IconActionButton;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.hisun.codeassistant.utils.file.FileUtil.findLanguageExtensionMapping;
import static java.lang.String.format;
import com.hisun.codeassistant.utils.EditorUtil;

public class ResponseEditorPanel extends JPanel implements Disposable {
    private final Editor editor;

    public ResponseEditorPanel(
            Project project,
            String code,
            String markdownLanguage,
            boolean readOnly,
            Disposable disposableParent) {
        super(new BorderLayout());
        setBorder(JBUI.Borders.empty(8, 0));
        setOpaque(false);

        editor = EditorUtil.createEditor(
                project,
                findLanguageExtensionMapping(markdownLanguage).getValue(),
                code);

        var group = new DefaultActionGroup();
        group.add(new ReplaceCodeInMainEditorAction());
        String originalGroupId = ((EditorEx) editor).getContextMenuGroupId();
        if (originalGroupId != null) {
            AnAction originalGroup = ActionManager.getInstance().getAction(originalGroupId);
            if (originalGroup instanceof ActionGroup) {
                group.addAll(((ActionGroup) originalGroup).getChildren(null));
            }
        }
        configureEditor(
                (EditorEx) editor,
                readOnly,
                new ContextMenuPopupHandler.Simple(group),
                findLanguageExtensionMapping(markdownLanguage).getValue());
        add(editor.getComponent(), BorderLayout.CENTER);

        Disposer.register(disposableParent, this);
    }

    @Override
    public void dispose() {
        EditorFactory.getInstance().releaseEditor(editor);
    }

    public Editor getEditor() {
        return editor;
    }

    private void configureEditor(
            EditorEx editorEx,
            boolean readOnly,
            ContextMenuPopupHandler popupHandler,
            String extension) {
        if (readOnly) {
            editorEx.setOneLineMode(true);
            editorEx.setHorizontalScrollbarVisible(false);
        }
        editorEx.installPopupHandler(popupHandler);
        editorEx.setColorsScheme(EditorColorsManager.getInstance().getSchemeForCurrentUITheme());

        var settings = editorEx.getSettings();
        settings.setAdditionalColumnsCount(0);
        settings.setAdditionalLinesCount(0);
        settings.setAdditionalPageAtBottom(false);
        settings.setVirtualSpace(false);
        settings.setUseSoftWraps(false);
        settings.setLineMarkerAreaShown(false);
        settings.setGutterIconsShown(false);
        settings.setLineNumbersShown(false);

        editorEx.getGutterComponentEx().setVisible(true);
        editorEx.getGutterComponentEx().getParent().setVisible(false);
        editorEx.setVerticalScrollbarVisible(false);
        editorEx.getContentComponent().setBorder(JBUI.Borders.emptyLeft(4));
        editorEx.setBorder(IdeBorderFactory.createBorder(ColorUtil.fromHex("#48494b")));
        editorEx.setPermanentHeaderComponent(createHeaderComponent(editorEx, extension));
        editorEx.setHeaderComponent(null);
    }

    private JPanel createHeaderComponent(EditorEx editorEx, String extension) {
        var headerComponent = new JPanel(new BorderLayout());
        headerComponent.setBorder(
                JBUI.Borders.compound(
                        JBUI.Borders.customLine(ColorUtil.fromHex("#48494b"), 1, 1, 0, 1),
                        JBUI.Borders.empty(4)));
        headerComponent.add(createExpandLink(editorEx), BorderLayout.LINE_START);
        headerComponent.add(createHeaderActions(extension, editorEx).getComponent(),
                BorderLayout.LINE_END);
        return headerComponent;
    }

    private String getLinkText(boolean expanded) {
        return expanded
                ? format(
                HiCodeAssistantBundle.get("toolwindow.chat.editor.action.expand"),
                ((EditorEx) editor).getDocument().getLineCount() - 1)
                : HiCodeAssistantBundle.get("toolwindow.chat.editor.action.collapse");
    }

    private ActionLink createExpandLink(EditorEx editorEx) {
        var linkText = getLinkText(editorEx.isOneLineMode());
        var expandLink = new ActionLink(
                linkText,
                event -> {
                    var oneLineMode = editorEx.isOneLineMode();
                    var source = (ActionLink) event.getSource();
                    source.setText(getLinkText(!oneLineMode));
                    source.setIcon(oneLineMode ? AllIcons.General.ArrowDown : AllIcons.General.ArrowRight);

                    editorEx.setOneLineMode(!oneLineMode);
                    editorEx.setHorizontalScrollbarVisible(oneLineMode);
                    editorEx.getContentComponent().revalidate();
                    editorEx.getContentComponent().repaint();
                });
        expandLink.setIcon(editorEx.isOneLineMode() ? AllIcons.General.ArrowRight : AllIcons.General.ArrowDown);
        return expandLink;
    }

    private ActionToolbar createHeaderActions(String extension, EditorEx editorEx) {
        var actionGroup = new DefaultCompactActionGroup("EDITOR_TOOLBAR_ACTION_GROUP", false);
        actionGroup.add(new CopyAction(editor));
        actionGroup.add(new ReplaceSelectionAction(editor));
        actionGroup.addSeparator();

        var wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.add(new IconActionButton(new CopyAction(editor)));
        wrapper.add(Box.createHorizontalStrut(4));
        wrapper.add(new IconActionButton(new ReplaceSelectionAction(editor)));

        var menu = new JBPopupMenu();
        menu.add(new JBMenuItem(new DiffAction(editorEx, menu.getLocation())));
        menu.add(new JBMenuItem(new EditAction(editorEx)));
        menu.add(new JBMenuItem(new NewFileAction(editorEx, extension)));

        var toolbar = ActionManager.getInstance()
                .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
        actionGroup.add(new AnAction("Editor Actions", "Editor Actions", AllIcons.General.GearPlain) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                menu.show(e.getInputEvent().getComponent(), 0, 0);
            }
        });
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        toolbar.setTargetComponent(editorEx.getComponent());
        toolbar.getComponent().setBorder(JBUI.Borders.empty());
        return toolbar;
    }
}
