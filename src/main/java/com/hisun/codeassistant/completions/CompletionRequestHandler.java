package com.hisun.codeassistant.completions;

import com.hisun.codeassistant.llms.client.openai.api.OpenAiError;
import com.hisun.codeassistant.llms.completion.CompletionEventListener;
import com.hisun.codeassistant.llms.completion.SerpCompletionEventListener;
import com.hisun.codeassistant.llms.completion.SerpResult;
import com.hisun.codeassistant.settings.state.SettingsState;
import com.intellij.openapi.diagnostic.Logger;
import okhttp3.sse.EventSource;

import javax.swing.*;
import java.util.List;

public class CompletionRequestHandler {
    private static final Logger LOG = Logger.getInstance(CompletionRequestHandler.class);

    private final StringBuilder messageBuilder = new StringBuilder();
    private final CompletionResponseEventListener completionResponseEventListener;
    private SwingWorker<Void, String> swingWorker;
    private EventSource eventSource;

    public CompletionRequestHandler(CompletionResponseEventListener completionResponseEventListener) {
        this.completionResponseEventListener = completionResponseEventListener;
    }

    public void call(CallParameters callParameters) {
        swingWorker = new CompletionRequestWorker(callParameters);
        swingWorker.execute();
    }

    public void cancel() {
        if (eventSource != null) {
            eventSource.cancel();
        }
        swingWorker.cancel(true);
    }

    private EventSource startCall(
            CallParameters callParameters,
            CompletionEventListener eventListener) {
        try {
            return CompletionRequestService.getInstance()
                    .getChatCompletionAsync(callParameters, eventListener);
        } catch (Throwable ex) {
            handleCallException(ex);
            throw ex;
        }
    }

    private void handleCallException(Throwable ex) {
        var errorMessage = "Something went wrong";
        if (ex instanceof TotalUsageExceededException) {
            errorMessage =
                    "The length of the context exceeds the maximum limit that the model can handle. "
                            + "Try reducing the input message or maximum completion token size.";
        }
        completionResponseEventListener.handleError(new OpenAiError.OpenAiErrorDetails(errorMessage, null, null, null), ex);
    }

    private class CompletionRequestWorker extends SwingWorker<Void, String> {

        private final CallParameters callParameters;

        public CompletionRequestWorker(CallParameters callParameters) {
            this.callParameters = callParameters;
        }

        protected Void doInBackground() {
            try {
                eventSource = startCall(callParameters, new RequestCompletionEventListener());
            } catch (TotalUsageExceededException e) {
                completionResponseEventListener.handleTokensExceeded(
                        callParameters.getConversation(),
                        callParameters.getMessage());
            }
            return null;
        }

        protected void process(List<String> chunks) {
            callParameters.getMessage().setResponse(messageBuilder.toString());
            for (String text : chunks) {
                messageBuilder.append(text);
                completionResponseEventListener.handleMessage(text);
            }
        }

        class RequestCompletionEventListener implements SerpCompletionEventListener {

            @Override
            public void onSerpResults(List<SerpResult> results) {
                completionResponseEventListener.handleSerpResults(results, callParameters.getMessage());
            }

            @Override
            public void onMessage(String message) {
                publish(message);
            }

            @Override
            public void onComplete(StringBuilder messageBuilder) {
                completionResponseEventListener.handleCompleted(messageBuilder.toString(), callParameters);
            }

            @Override
            public void onError(OpenAiError.OpenAiErrorDetails error, Throwable ex) {
                completionResponseEventListener.handleError(error, ex);
            }
        }
    }
}
