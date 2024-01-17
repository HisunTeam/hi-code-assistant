package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.conversations.Conversation;
import com.hisun.codeassistant.conversations.message.Message;

public class CallParameters {
    private final Conversation conversation;
    private final ConversationType conversationType;
    private final Message message;
    private final boolean retry;

    public CallParameters(
            Conversation conversation,
            ConversationType conversationType,
            Message message,
            boolean retry) {
        this.conversation = conversation;
        this.conversationType = conversationType;
        this.message = message;
        this.retry = retry;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isRetry() {
        return retry;
    }
}
