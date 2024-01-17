package com.hisun.codeassistant

import com.hisun.codeassistant.actions.editor.EditorActionsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class HiCodeAssistantStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        EditorActionsUtil.refreshActions()
    }
}