package com.hisun.codeassistant.llms.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Search Engine Results Pages Result
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SerpResult {
    private final String url;
    private final String name;
    private final String snippet;
    private final String snippetSource;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SerpResult(
            @JsonProperty("url") String url,
            @JsonProperty("name") String name,
            @JsonProperty("snippet") String snippet,
            @JsonProperty("snippet_source") String snippetSource) {
        this.url = url;
        this.name = name;
        this.snippet = snippet;
        this.snippetSource = snippetSource;
    }

}
