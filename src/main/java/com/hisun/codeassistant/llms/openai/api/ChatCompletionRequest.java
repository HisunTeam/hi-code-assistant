package com.hisun.codeassistant.llms.openai.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionRequest {
    /**
     * ID of the model to use.
     */
    String model;
    /**
     * The messages to generate chat completions for, in the <a
     * href="https://platform.openai.com/docs/guides/chat/introduction">chat format</a>.<br>
     * see {@link ChatMessage}
     */
    List<ChatMessage> messages = new ArrayList<>();
    /**
     * If set, partial message deltas will be sent, like in ChatGPT. Tokens will be sent as data-only <a
     * href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format">server-sent
     * events</a> as they become available, with the stream terminated by a data: [DONE] message.
     */
    Boolean stream;
}
