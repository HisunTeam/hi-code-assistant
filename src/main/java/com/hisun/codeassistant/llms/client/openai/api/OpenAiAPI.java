package com.hisun.codeassistant.llms.client.openai.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAiAPI {
//    private static final URI gpt_service = URI.create("https://api.openai.com/v1/chat/completions");

    private static final String endpoint = "/v1/chat/completions";

    public HttpResponse<String> postToOpenAiApi(String requestBodyAsJson, String modelBaseHost)
            throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder().uri(URI.create(modelBaseHost + endpoint))
                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyAsJson)).build();
        final HttpClient client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
