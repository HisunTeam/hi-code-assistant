package com.hisun.codeassistant.codecompletions;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.settings.GeneralSettings;
import com.hisun.codeassistant.settings.service.ServiceType;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import okhttp3.sse.EventSource;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class CallDebouncer {
    private final Project project;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<Object, Future<?>> delayedMap = new ConcurrentHashMap<>();
    private final AtomicReference<EventSource> currentCall = new AtomicReference<>();

    public CallDebouncer(Project project) {
        this.project = project;
    }

    /**
     * Implements a debounce mechanism for {@code callable} with a specified {@code delay}. This means
     * the callable is set to execute after the given {@code delay} period. However, if this method is
     * invoked again with the same key before the {@code delay} elapses, the scheduled execution will
     * be aborted, and therefore the previous request will be cancelled.
     */
    public void debounce(Object key, CallRunnable runnable, long delay, TimeUnit unit) {
        cancelPreviousCall();

        Future<?> prev = delayedMap.put(key, scheduler.schedule(() -> {
            try {
                var progressIndicator =
                        ServiceType.SELF_HOSTED.equals(GeneralSettings.getCurrentState().getSelectedService())
                                ? createProgressIndicator()
                                : null;
                currentCall.set(runnable.call(progressIndicator));
            } finally {
                delayedMap.remove(key);
            }
        }, delay, unit));

        if (prev != null) {
            prev.cancel(true);
        }
    }

    public void shutdown() {
        cancelPreviousCall();
        scheduler.shutdownNow();
    }

    public void cancelPreviousCall() {
        var call = currentCall.get();
        if (call != null) {
            call.cancel();
        }
    }

    private BackgroundableProcessIndicator createProgressIndicator() {
        return new BackgroundableProcessIndicator(project,
                HiCodeAssistantBundle.get("codeCompletion.progress.title"), null, null, true) {
            @Override
            protected void onRunningChange() {
                if (isCanceled()) {
                    cancelPreviousCall();
                    HiCodeAssistantEditorManager.getInstance().disposeAllInlays(project);
                }
            }
        };
    }
}
