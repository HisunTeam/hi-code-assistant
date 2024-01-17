package com.hisun.codeassistant.toolwindows.chat.ui.textarea;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.completions.CompletionRequestHandler;
import com.hisun.codeassistant.utils.UIUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public class UserPromptTextArea extends JPanel {
    private static final Logger LOG = Logger.getInstance(UserPromptTextArea.class);

    private static final JBColor BACKGROUND_COLOR = JBColor.namedColor("Editor.SearchField.background", com.intellij.util.ui.UIUtil.getTextFieldBackground());

    private final JBTextArea textArea;

    private final int textAreaRadius = 16;
    private final Consumer<String> onSubmit;
    private JButton stopButton;
    private JPanel iconsPanel;
    private boolean submitEnabled = true;

    public UserPromptTextArea(Consumer<String> onSubmit, TotalTokensPanel totalTokensPanel) {
        super(new BorderLayout());
        this.onSubmit = onSubmit;

        textArea = new JBTextArea();
        textArea.getDocument().addDocumentListener(getDocumentAdapter(totalTokensPanel));
        textArea.setOpaque(false);
        textArea.setBackground(BACKGROUND_COLOR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.getEmptyText().setText(HiCodeAssistantBundle.get("toolwindow.chat.textArea.emptyText"));
        textArea.setBorder(JBUI.Borders.empty(8, 4));
        UIUtil.addShiftEnterInputMap(textArea, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    handleSubmit();
                } finally {
                    totalTokensPanel.updateUserPromptTokens("");
                }
            }
        });
        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                UserPromptTextArea.super.paintBorder(UserPromptTextArea.super.getGraphics());
            }

            @Override
            public void focusLost(FocusEvent e) {
                UserPromptTextArea.super.paintBorder(UserPromptTextArea.super.getGraphics());
            }
        });
        textArea.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                iconsPanel.getComponents()[0].setEnabled(e.getDocument().getLength() > 0);
            }
        });
        updateFont();
        init();
    }

    public String getText() {
        return textArea.getText().trim();
    }

    public void focus() {
        textArea.requestFocus();
        textArea.requestFocusInWindow();
    }

    private DocumentAdapter getDocumentAdapter(TotalTokensPanel totalTokensPanel) {
        return new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                if (submitEnabled) {
                    try {
                        var document = event.getDocument();
                        var text = document.getText(
                                document.getStartPosition().getOffset(),
                                document.getEndPosition().getOffset() - 1);
                        totalTokensPanel.updateUserPromptTokens(text);
                    } catch (BadLocationException ex) {
                        LOG.error("Something went wrong while processing user input tokens", ex);
                    }
                }
            }
        };
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(JBUI.CurrentTheme.ActionButton.focusedBorder());
        if (textArea.isFocusOwner()) {
            g2.setStroke(new BasicStroke(1.5F));
        }
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
    }

    @Override
    public Insets getInsets() {
        return JBUI.insets(6, 12, 6, 6);
    }

    public void setSubmitEnabled(boolean submitEnabled) {
        this.submitEnabled = submitEnabled;
        stopButton.setEnabled(!submitEnabled);
    }

    private void handleSubmit() {
        if (submitEnabled && !textArea.getText().isEmpty()) {
            // Replacing each newline with two newlines to ensure proper Markdown formatting
            var text = textArea.getText().replace("\n", "\n\n");
            onSubmit.accept(text.trim());
            textArea.setText("");
        }
    }

    private void init() {
        setOpaque(false);
        add(textArea, BorderLayout.CENTER);

        stopButton = createIconButton(AllIcons.Actions.Suspend, null);

        var flowLayout = new FlowLayout(FlowLayout.RIGHT);
        flowLayout.setHgap(8);
        iconsPanel = new JPanel(flowLayout);
        iconsPanel.add(createIconButton(HiCodeAssistantIcons.SEND_ICON, this::handleSubmit));
        iconsPanel.add(stopButton);
        add(iconsPanel, BorderLayout.EAST);
    }

    private void updateFont() {
        if (Registry.is("ide.find.use.editor.font", false)) {
            textArea.setFont(EditorUtil.getEditorFont());
        } else {
            textArea.setFont(UIManager.getFont("TextField.font"));
        }
    }

    // TODO: IconActionButton?
    private JButton createIconButton(Icon icon, @Nullable Runnable submitListener) {
        var button = UIUtil.createIconButton(icon);
        if (submitListener != null) {
            button.addActionListener((e) -> handleSubmit());
        }
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setEnabled(false);
        return button;
    }

    public void setRequestHandler(@NotNull CompletionRequestHandler requestService) {
        stopButton.addActionListener(e -> requestService.cancel());
    }
}
