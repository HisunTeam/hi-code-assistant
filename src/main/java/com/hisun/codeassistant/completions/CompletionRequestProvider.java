package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.EncodingManager;
import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.llms.client.openai.api.ChatMessageRole;
import com.hisun.codeassistant.llms.client.self.SelfModelEnum;
import com.hisun.codeassistant.llms.client.openai.api.ChatCompletionRequest;
import com.hisun.codeassistant.llms.client.openai.api.ChatMessage;
import com.hisun.codeassistant.llms.client.openai.completion.OpenAIChatCompletionModel;
import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.hisun.codeassistant.utils.file.FileUtil.getResourceContent;
import static java.util.stream.Collectors.toList;

public class CompletionRequestProvider {
    private static final Logger LOG = Logger.getInstance(CompletionRequestProvider.class);

    public static final String COMPLETION_SYSTEM_PROMPT = getResourceContent(
            "/prompts/default-completion-system-prompt.txt");
    public static final String FIX_COMPILE_ERRORS_SYSTEM_PROMPT = getResourceContent(
            "/prompts/fix-compile-errors.txt");
    private final EncodingManager encodingManager = EncodingManager.getInstance();
    private final Conversation conversation;

    public CompletionRequestProvider(Conversation conversation) {
        this.conversation = conversation;
    }

    public ChatCompletionRequest buildOpenAIChatCompletionRequest(
            @Nullable String model,
            CallParameters callParameters) {
        return ChatCompletionRequest.builder()
                .messages(buildMessages(model, callParameters))
                .model(model)
                .maxTokens(ConfigurationState.getInstance().getMaxTokens())
                .temperature(ConfigurationState.getInstance().getTemperature())
                .stream(Boolean.TRUE)
                .build();
    }

    public ChatCompletionRequest buildSelfChatCompletionRequest(
            @Nullable String model,
            CallParameters callParameters) {
        String modelCode = SelfModelEnum.fromName(model).getCode();
        return ChatCompletionRequest.builder()
                .messages(buildSelfMessages(modelCode, callParameters))
                .model(modelCode)
                .maxTokens(ConfigurationState.getInstance().getMaxTokens())
                .temperature(ConfigurationState.getInstance().getTemperature())
                .stream(Boolean.TRUE)
                .build();
    }

    public List<ChatMessage> buildMessages(CallParameters callParameters) {
        var message = callParameters.getMessage();
        var messages = new ArrayList<ChatMessage>();

        if (callParameters.getConversationType() == ConversationType.DEFAULT) {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), ConfigurationState.getInstance().getSystemPrompt()));
        }
        if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), FIX_COMPILE_ERRORS_SYSTEM_PROMPT));
        }

        for (var prevMessage : conversation.getMessages()) {
            if (callParameters.isRetry() && prevMessage.getId().equals(message.getId())) {
                break;
            }
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prevMessage.getPrompt()));
            messages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), prevMessage.getResponse()));
        }
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), message.getPrompt()));
        return messages;
    }

    private List<ChatMessage> buildMessages(
            @Nullable String model,
            CallParameters callParameters) {
        var messages = buildMessages(callParameters);

        int totalUsage = messages.parallelStream()
                .mapToInt(encodingManager::countMessageTokens)
                .sum() + ConfigurationState.getInstance().getMaxTokens();

        int modelMaxTokens;
        try {
            modelMaxTokens = OpenAIChatCompletionModel.findByCode(model).getMaxTokens();

            if (totalUsage <= modelMaxTokens) {
                return messages;
            }
        } catch (NoSuchElementException ex) {
            return messages;
        }
        return tryReducingMessagesOrThrow(messages, totalUsage, modelMaxTokens);
    }

    private List<ChatMessage> buildSelfMessages(
            @Nullable String model,
            CallParameters callParameters) {
        var messages = buildMessages(callParameters);

        int totalUsage = messages.parallelStream()
                .mapToInt(encodingManager::countMessageTokens)
                .sum() + ConfigurationState.getInstance().getMaxTokens();

        int modelMaxTokens;
        try {
            modelMaxTokens = SelfModelEnum.fromName(model).getMaxTokens();

            if (totalUsage <= modelMaxTokens) {
                return messages;
            }
        } catch (NoSuchElementException ex) {
            return messages;
        }
        return tryReducingMessagesOrThrow(messages, totalUsage, modelMaxTokens);
    }

    private List<ChatMessage> tryReducingMessagesOrThrow(
            List<ChatMessage> messages,
            int totalUsage,
            int modelMaxTokens) {
        if (!ConversationsState.getInstance().discardAllTokenLimits) {
            if (!conversation.isDiscardTokenLimit()) {
                throw new TotalUsageExceededException();
            }
        }

        // skip the system prompt
        for (int i = 1; i < messages.size(); i++) {
            if (totalUsage <= modelMaxTokens) {
                break;
            }

            messages.set(i, null);
        }

        return messages.stream().filter(Objects::nonNull).collect(toList());
    }
}
