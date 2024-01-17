package com.hisun.codeassistant.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.Nullable;

public class ApplicationUtil {
    @Nullable
    public static Project findCurrentProject() {
        IdeFrame frame = IdeFocusManager.getGlobalInstance().getLastFocusedFrame();
        Project project = frame != null ? frame.getProject() : null;
        if (isValidProject(project)) {
            return project;
        }
        return findProjectFromOpenProjects();
    }

    private static Project findProjectFromOpenProjects() {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (isValidProject(project)) {
                return project;
            }
        }
        return null;
    }

    private static boolean isValidProject(@Nullable Project project) {
        return project != null && !project.isDisposed() && !project.isDefault();
    }
}
