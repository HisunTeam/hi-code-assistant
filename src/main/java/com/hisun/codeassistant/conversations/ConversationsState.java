package com.hisun.codeassistant.conversations;

import com.hisun.codeassistant.conversations.converter.ConversationConverter;
import com.hisun.codeassistant.conversations.converter.ConversationsConverter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@State(name = "HiCodeAssistant_ConversationsState_0206", storages = @Storage("HiCodeAssistant_ConversationsState_0206.xml"))
public class ConversationsState implements PersistentStateComponent<ConversationsState> {
    @OptionTag(converter = ConversationsConverter.class)
    public ConversationsContainer conversationsContainer = new ConversationsContainer();

    @OptionTag(converter = ConversationConverter.class)
    public Conversation currentConversation;

    public boolean discardAllTokenLimits;

    public static ConversationsState getInstance() {
        return ApplicationManager.getApplication().getService(ConversationsState.class);
    }
    @Override
    public @Nullable ConversationsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ConversationsState conversationsState) {
        XmlSerializerUtil.copyBean(conversationsState, this);
    }
    public void discardAllTokenLimits() {
        this.discardAllTokenLimits = true;
    }

    public void setCurrentConversation(@Nullable Conversation conversation) {
        this.currentConversation = conversation;
    }

    public static @Nullable Conversation getCurrentConversation() {
        return getInstance().currentConversation;
    }

    public Map<String, List<Conversation>> getConversationsMapping() {
        return conversationsContainer.getConversationsMapping();
    }
}
