package com.hisun.codeassistant.actions.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class HiCodeAssistantNotification {
    public static final String HI_CODE_ASSISTANT_NOTIFICATION_GROUP = "HiCodeAssistant Notification Group";
    public static final String HI_CODE_ASSISTANT = "HiCodeAssistant通知组";

    public static void info(String content) {
        var notification = new Notification(
                HI_CODE_ASSISTANT_NOTIFICATION_GROUP,
                HI_CODE_ASSISTANT,
                content,
                NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }

    public static void warn(String content) {
        var notification = new Notification(
                HI_CODE_ASSISTANT_NOTIFICATION_GROUP,
                HI_CODE_ASSISTANT,
                content,
                NotificationType.WARNING);
        Notifications.Bus.notify(notification);
    }

    public static void error(String content) {
        var notification = new Notification(
                HI_CODE_ASSISTANT_NOTIFICATION_GROUP,
                HI_CODE_ASSISTANT,
                content,
                NotificationType.ERROR);
        Notifications.Bus.notify(notification);
    }
}
