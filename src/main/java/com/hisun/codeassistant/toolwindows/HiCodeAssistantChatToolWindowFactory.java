package com.hisun.codeassistant.toolwindows;

import com.google.common.collect.Maps;
import com.hisun.codeassistant.actions.toolbar.ToolbarClearAction;
import com.hisun.codeassistant.toolwindows.chat.HiCodeAssistantChatToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Deprecated
public class HiCodeAssistantChatToolWindowFactory implements ToolWindowFactory {
    private static final Map<Project, HiCodeAssistantChatToolWindow> hiCodeAssistantChatToolWindowMap = Maps.newConcurrentMap();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        HiCodeAssistantChatToolWindow hiCodeAssistantChatToolWindow = new HiCodeAssistantChatToolWindow(project);
        hiCodeAssistantChatToolWindowMap.put(project, hiCodeAssistantChatToolWindow);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(hiCodeAssistantChatToolWindow.getHiCodeAssistantChatToolWindowPanel(), "", false);

        toolWindow.getContentManager().addContent(content);

        toolWindow.setTitleActions(List.of(new ToolbarClearAction()));
    }

    public static HiCodeAssistantChatToolWindow getHiCodeAssistantChatToolWindow(@NotNull Project project) {
        return hiCodeAssistantChatToolWindowMap.get(project);
    }
}
