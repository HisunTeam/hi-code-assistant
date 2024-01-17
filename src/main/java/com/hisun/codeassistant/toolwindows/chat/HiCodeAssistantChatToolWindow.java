package com.hisun.codeassistant.toolwindows.chat;

import com.hisun.codeassistant.constant.PromptConst;
import com.hisun.codeassistant.enums.ChatMessageRole;
import com.hisun.codeassistant.enums.EditorActionEnum;
import com.hisun.codeassistant.enums.SessionTypeEnum;
import com.hisun.codeassistant.llms.LlmProvider;
import com.hisun.codeassistant.llms.LlmProviderFactory;
import com.hisun.codeassistant.llms.openai.api.ChatCompletionRequest;
import com.hisun.codeassistant.llms.openai.api.ChatMessage;
import com.hisun.codeassistant.settings.state.HiCodeAssistantSettingsState;
import com.hisun.codeassistant.toolwindows.components.ChatDisplayPanel;
import com.hisun.codeassistant.toolwindows.components.ContentComponent;
import com.hisun.codeassistant.toolwindows.components.EditorInfo;
import com.hisun.codeassistant.toolwindows.components.UserChatPanel;
import com.hisun.codeassistant.utils.MarkdownUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class HiCodeAssistantChatToolWindow {

    public static final String FETCH_ANSWER = "获取答案";
    @Getter
    private final JPanel hiCodeAssistantChatToolWindowPanel;
    private final UserChatPanel userChatPanel;
    private final ScrollablePanel chatContentPanel;
    private final Project project;
    private LlmProvider llmProvider;
    private final ChatCompletionRequest multiSessionRequest = new ChatCompletionRequest();

    public HiCodeAssistantChatToolWindow(Project project) {
        this.project = project;
        this.hiCodeAssistantChatToolWindowPanel = new JPanel(new GridBagLayout());
        this.chatContentPanel = new ScrollablePanel();
        this.userChatPanel = new UserChatPanel(this::syncSendAndDisplay, this::stopSending);

        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        chatContentPanel.setLayout(new BoxLayout(chatContentPanel, BoxLayout.Y_AXIS));
        var scrollPane = new JBScrollPane(chatContentPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);
        hiCodeAssistantChatToolWindowPanel.add(scrollPane, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 1;

        hiCodeAssistantChatToolWindowPanel.add(userChatPanel, gbc);
        chatContentPanel.add(createWelcomePanel());
    }

    private void showChatContent(String content, ChatMessageRole role, EditorActionEnum actionType, EditorInfo editorInfo) {
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        ContentComponent contentPanel = new ContentComponent();

        if (role == ChatMessageRole.SYSTEM && actionType == EditorActionEnum.GENERATE_COMMENTS) {
            contentPanel.add(contentPanel.createTextComponent("请看 diff view 窗口"));
        } else if (role == ChatMessageRole.USER && actionType != null) {
            contentPanel.add(contentPanel.createRightActionComponent(actionType.getLabel(), project, editorInfo));
        } else {
            List<String> blocks = MarkdownUtil.divideMarkdown(content);
            for (String block : blocks) {
                if (block.startsWith("```")) {
                    contentPanel.add(contentPanel.createCodeComponent(project, block, actionType,
                            editorInfo == null ? null : editorInfo.getChosenEditor()));
                } else {
                    contentPanel.add(contentPanel.createTextComponent(block));
                }
            }
        }

        ChatDisplayPanel chatDisplayPanel = new ChatDisplayPanel().setText(contentPanel);

        if (role == ChatMessageRole.USER) {
            chatDisplayPanel.setUserLabel();
        } else {
            chatDisplayPanel.setSystemLabel();
        }

        chatContentPanel.add(chatDisplayPanel, gbc);
        chatContentPanel.revalidate();
        chatContentPanel.repaint();

        // scroll to bottom
        chatContentPanel.scrollRectToVisible(chatContentPanel.getVisibleRect());
    }

    private String sendMessage(Integer sessionType, String message) {
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        // check session type,default multi session
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        SessionTypeEnum sessionTypeEnum = SessionTypeEnum.getEnumByCode(sessionType);
        if (SessionTypeEnum.INDEPENDENT.equals(sessionTypeEnum)) {
            // independent message can not update, just readonly
            chatCompletionRequest.getMessages().add(chatMessage);
            chatCompletionRequest.getMessages().add(new ChatMessage(ChatMessageRole.SYSTEM.value(), PromptConst.RESPONSE_FORMAT));
        } else {
            if (multiSessionRequest.getMessages().isEmpty()) {
                multiSessionRequest.getMessages().add(new ChatMessage(ChatMessageRole.SYSTEM.value(), PromptConst.CHINESE_RESPONSE_FORMAT));
            }
            chatCompletionRequest.setStream(multiSessionRequest.getStream());
            chatCompletionRequest.setModel(multiSessionRequest.getModel());
            multiSessionRequest.getMessages().add(chatMessage);
            chatCompletionRequest.getMessages().addAll(multiSessionRequest.getMessages());
        }

        var llmProvider = new LlmProviderFactory().getLlmProvider(project);
        this.llmProvider = llmProvider;

        String chatCompletion = llmProvider.chatCompletion(chatCompletionRequest);
        if (SessionTypeEnum.MULTI_TURN.equals(sessionTypeEnum) &&
                chatCompletionRequest.getMessages().size() > multiSessionRequest.getMessages().size()) {
            // update multi session request
            multiSessionRequest.getMessages().add(
                    chatCompletionRequest.getMessages().get(chatCompletionRequest.getMessages().size() - 1));
        }
        return chatCompletion;
    }

    public void syncSendAndDisplay(String message) {
        // support multi session
        syncSendAndDisplay(SessionTypeEnum.MULTI_TURN.getCode(), null, message, null, null);
    }

    public void syncSendAndDisplay(Integer sessionType, EditorActionEnum editorActionEnum, String message, Consumer<String> callback, EditorInfo editorInfo) {

        if (userChatPanel.isSending()) {
            return;
        }

        userChatPanel.setSending(true);
        userChatPanel.setIconStop();

        // show prompt
        showChatContent(message, ChatMessageRole.USER, editorActionEnum, editorInfo);

        // show thinking
        showChatContent("正在思考中...", ChatMessageRole.SYSTEM, null, null);

        Task.Backgroundable task = new Task.Backgroundable(project, FETCH_ANSWER, true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                if (Objects.isNull(editorActionEnum)) {
                    progressIndicator.setText("获取答案");
                } else {
                    progressIndicator.setText(editorActionEnum.getLabel());
                }
                if (!progressIndicator.isRunning())
                    progressIndicator.start();
                String result = sendMessage(sessionType, message);
                progressIndicator.stop();

                SwingUtilities.invokeLater(() -> {
                    int componentCount = chatContentPanel.getComponentCount();
                    if (componentCount > 0) {
                        Component loading = chatContentPanel.getComponent(componentCount - 1);
                        chatContentPanel.remove(loading);
                        showChatContent(result, ChatMessageRole.SYSTEM, editorActionEnum, editorInfo);
                    }

                    userChatPanel.setIconSend();
                    userChatPanel.setSending(false);

                    if (callback != null) {
                        callback.accept(result);
                    }
                });
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private void stopSending() {
        if (llmProvider == null) {
            return;
        }
        userChatPanel.setIconSend();
        userChatPanel.setSending(false);
    }

    public void clearSession() {
        SwingUtilities.invokeLater(() -> {
            if (userChatPanel.isSending()) {
                stopSending();
            }
            chatContentPanel.setVisible(false);
            chatContentPanel.removeAll();
            chatContentPanel.setVisible(true);
            chatContentPanel.add(createUserPromptPanel());
        });
        multiSessionRequest.getMessages().clear();
    }

    public void addClearSessionInfo() {
        if (multiSessionRequest.getMessages().isEmpty()) {
            return;
        }
        multiSessionRequest.getMessages().clear();
        JTextPane clearSessionTip = new JTextPane();
        clearSessionTip.setText("会话已清除");
        chatContentPanel.add(new ChatDisplayPanel().setText(clearSessionTip).setSystemLabel());
    }

    private ChatDisplayPanel createWelcomePanel() {
        JTextPane welcomePanel = new JTextPane();
        welcomePanel.setContentType("text/html");
        welcomePanel.setEditable(false);
        welcomePanel.setOpaque(true);
        welcomePanel.setBackground(new JBColor(Gray._248, Gray._54));
        welcomePanel.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
        welcomePanel.setText(String.format("欢迎您 @<span style=\"font-weight: bold;\">%s</span>! \n" +
                "很高兴您的使用， \n" +
                "我是您值得信赖的助理，随时准备帮助您更有效地完成任务。\n" +
                "<br><br>\n" +
                "您可以提出一般性问题，但我真正的专业知识在于帮助您满足编码需求.\n" +
                "作为一名人工智能助手，我努力提供最好的帮助，\n" +
                "但是请记住，偶尔可能会出现意外或错误，\n" +
                "仔细检查任何生成的代码或建议总是一个好主意。", HiCodeAssistantSettingsState.getInstance().getFullName()));
        welcomePanel.setBorder(JBUI.Borders.emptyLeft(5));
        ChatDisplayPanel chatDisplayPanel = new ChatDisplayPanel().setText(welcomePanel);
        chatDisplayPanel.setSystemLabel();
        return chatDisplayPanel;
    }

    private ChatDisplayPanel createUserPromptPanel() {
        JTextPane userPromptPanel = new JTextPane();
        userPromptPanel.setContentType("text/html");
        userPromptPanel.setEditable(false);
        userPromptPanel.setOpaque(true);
        userPromptPanel.setBackground(new JBColor(Gray._248, Gray._54));
        userPromptPanel.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
        userPromptPanel.setBorder(JBUI.Borders.emptyLeft(5));
        userPromptPanel.setText(String.format("您好 @<span style=\"font-weight: bold;\">%s</span>， 我有什么可以帮助您吗？\n" +
                "<br><br>\n" +
                "作为一个人工智能助手，我会努力提供最好的帮助，\n" +
                "但是请记住，偶尔我也会出现意外或错误，\n" +
                "仔细检查任何生成的代码或建议总是一个好主意。", HiCodeAssistantSettingsState.getInstance().getFullName()));
        ChatDisplayPanel chatDisplayPanel = new ChatDisplayPanel().setText(userPromptPanel);
        chatDisplayPanel.setSystemLabel();
        return chatDisplayPanel;
    }
}
