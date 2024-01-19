package com.hisun.codeassistant.toolwindows.chat.standard;


import com.hisun.codeassistant.actions.editor.EditorActionEnum;

import java.awt.*;

@FunctionalInterface
public interface EditorActionEvent {
    void handleAction(EditorActionEnum action, Point locationOnScreen);
}
