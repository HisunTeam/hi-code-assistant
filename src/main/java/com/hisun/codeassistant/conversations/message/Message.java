package com.hisun.codeassistant.conversations.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hisun.codeassistant.llms.completion.SerpResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message {
    private final UUID id;
    @Setter
    private String prompt;
    @Setter
    private String response;
    @Setter
    private String userMessage;
    @Setter
    private List<SerpResult> serpResults;

    public Message(String prompt, String response) {
        this(prompt);
        this.response = response;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Message(@JsonProperty("prompt") String prompt) {
        this.id = UUID.randomUUID();
        this.prompt = prompt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Message)) {
            return false;
        }
        Message other = (Message) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prompt);
    }
}
