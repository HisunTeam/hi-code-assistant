package com.hisun.codeassistant

import com.hisun.codeassistant.actions.editor.EditorActionsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupActivity


class HiCodeAssistantStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        EditorActionsUtil.refreshActions()
    }
}