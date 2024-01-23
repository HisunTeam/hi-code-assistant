package com.hisun.codeassistant.conversations;

import com.hisun.codeassistant.completions.CallParameters;
import com.hisun.codeassistant.conversations.message.Message;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.hisun.codeassistant.settings.state.OpenAISettingsState;
import com.hisun.codeassistant.settings.state.SelfHostedLanguageModelSettingsState;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public final class ConversationService {
    private final ConversationsState conversationState = ConversationsState.getInstance();

    private ConversationService() {
    }

    public static ConversationService getInstance() {
        return ApplicationManager.getApplication().getService(ConversationService.class);
    }

    public List<Conversation> getSortedConversations() {
        return conversationState.getConversationsMapping()
                .values()
                .stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Conversation::getUpdatedOn).reversed())
                .collect(toList());
    }

    public Conversation createConversation(String clientCode) {
        var settings = SettingsState.getInstance();
        var conversation = new Conversation();
        conversation.setId(UUID.randomUUID());
        conversation.setClientCode(clientCode);
        conversation.setCreatedOn(LocalDateTime.now());
        conversation.setUpdatedOn(LocalDateTime.now());
        conversation.setModel(getModelForSelectedService(settings.getSelectedService()));
        return conversation;
    }

    public void addConversation(Conversation conversation) {
        var conversationsMapping = conversationState.getConversationsMapping();
        var conversations = conversationsMapping.get(conversation.getClientCode());
        if (conversations == null) {
            conversations = new ArrayList<>();
        }
        conversations.add(conversation);
        conversationsMapping.put(conversation.getClientCode(), conversations);
    }

    public void saveMessage(String response, CallParameters callParameters) {
        var conversation = callParameters.getConversation();
        var message = callParameters.getMessage();
        var conversationMessages = conversation.getMessages();
        if (callParameters.isRetry() && !conversationMessages.isEmpty()) {
            var messageToBeSaved = conversationMessages.stream()
                    .filter(item -> item.getId().equals(message.getId()))
                    .findFirst().orElseThrow();
            messageToBeSaved.setResponse(response);
            saveConversation(conversation);
            return;
        }

        message.setResponse(response);
        conversation.addMessage(message);
        saveConversation(conversation);
    }

    public void saveMessage(@NotNull Conversation conversation, @NotNull Message message) {
        conversation.setUpdatedOn(LocalDateTime.now());
        var iterator = getIterator(conversation.getClientCode());
        while (iterator.hasNext()) {
            var next = iterator.next();
            next.setMessages(
                    next.getMessages().stream().map(item -> {
                        if (item.getId() == message.getId()) {
                            return message;
                        }
                        return item;
                    }).collect(toList()));
            if (next.getId().equals(conversation.getId())) {
                iterator.set(conversation);
            }
        }
    }

    public void saveConversation(Conversation conversation) {
        conversation.setUpdatedOn(LocalDateTime.now());
        var iterator = getIterator(conversation.getClientCode());
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (next.getId().equals(conversation.getId())) {
                iterator.set(conversation);
            }
        }
        conversationState.setCurrentConversation(conversation);
    }

    public Conversation startConversation() {
        var completionCode = SettingsState.getInstance().getSelectedService().getCompletionCode();
        var conversation = createConversation(completionCode);
        conversationState.setCurrentConversation(conversation);
        addConversation(conversation);
        return conversation;
    }

    public void updateConversation(Conversation conversation) {
        var selectedService = SettingsState.getInstance().getSelectedService();
        conversation.setClientCode(selectedService.getCompletionCode());
        conversation.setUpdatedOn(LocalDateTime.now());
        conversation.setModel(getModelForSelectedService(selectedService));
    }

    public void clearAll() {
        conversationState.getConversationsMapping().clear();
        conversationState.setCurrentConversation(null);
    }

    public void deleteConversation(Conversation conversation) {
        var iterator = getIterator(conversation.getClientCode());
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (next.getId().equals(conversation.getId())) {
                iterator.remove();
                break;
            }
        }
    }

    public void deleteSelectedConversation() {
        var nextConversation = getPreviousConversation();
        if (nextConversation.isEmpty()) {
            nextConversation = getNextConversation();
        }

        var currentConversation = ConversationsState.getCurrentConversation();
        if (currentConversation != null) {
            deleteConversation(currentConversation);
            nextConversation.ifPresent(conversationState::setCurrentConversation);
        } else {
            throw new RuntimeException("Tried to delete a conversation that hasn't been set");
        }
    }

    public void discardTokenLimits(Conversation conversation) {
        conversation.discardTokenLimits();
        saveConversation(conversation);
    }

    public Optional<Conversation> getPreviousConversation() {
        return tryGetNextOrPreviousConversation(true);
    }

    public Optional<Conversation> getNextConversation() {
        return tryGetNextOrPreviousConversation(false);
    }

    private ListIterator<Conversation> getIterator(String clientCode) {
        return conversationState.getConversationsMapping()
                .get(clientCode)
                .listIterator();
    }

    private Optional<Conversation> tryGetNextOrPreviousConversation(boolean isPrevious) {
        var currentConversation = ConversationsState.getCurrentConversation();
        if (currentConversation != null) {
            var sortedConversations = getSortedConversations();
            for (int i = 0; i < sortedConversations.size(); i++) {
                var conversation = sortedConversations.get(i);
                if (conversation != null && conversation.getId().equals(currentConversation.getId())) {
                    // higher index indicates older conversation
                    var previousIndex = isPrevious ? i + 1 : i - 1;
                    if (isPrevious ? previousIndex < sortedConversations.size() : previousIndex != -1) {
                        return Optional.of(sortedConversations.get(previousIndex));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static String getModelForSelectedService(ServiceType serviceType) {
        return switch (serviceType) {
            case OPENAI -> OpenAISettingsState.getInstance().getModel();
            case SELF_HOSTED -> SelfHostedLanguageModelSettingsState.getInstance().getModel();
        };
    }
}
