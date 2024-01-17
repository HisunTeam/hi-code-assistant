package com.hisun.codeassistant.toolwindows.chat.standard;


import java.awt.*;

@FunctionalInterface
public interface EditorActionEvent {
    void handleAction(EditorAction action, Point locationOnScreen);
}
