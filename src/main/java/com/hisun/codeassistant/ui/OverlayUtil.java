package com.hisun.codeassistant.ui;

import com.hisun.codeassistant.HiCodeAssistantBundle;
import com.hisun.codeassistant.conversations.ConversationsState;
import com.hisun.codeassistant.HiCodeAssistantIcons;
import com.hisun.codeassistant.settings.configuration.ConfigurationState;
import com.hisun.codeassistant.utils.EditorUtil;
import com.intellij.execution.ExecutionBundle;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static com.intellij.openapi.ui.Messages.CANCEL;
import static com.intellij.openapi.ui.Messages.OK;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class OverlayUtil {
    public static Notification getDefaultNotification(String content, NotificationType type) {
        return new Notification("HiCodeAssistant Notification Group", "HiCodeAssistant", content, type);
    }

    public static void showNotification(String content, NotificationType type) {
        Notifications.Bus.notify(getDefaultNotification(content, type));
    }

    public static int showDeleteConversationDialog() {
        return Messages.showYesNoDialog(
                HiCodeAssistantBundle.get("dialog.deleteConversation.description"),
                HiCodeAssistantBundle.get("dialog.deleteConversation.title"),
                HiCodeAssistantIcons.SYSTEM_ICON);
    }

    public static int showTokenLimitExceededDialog() {
        return MessageDialogBuilder.okCancel(
                        HiCodeAssistantBundle.get("dialog.tokenLimitExceeded.title"),
                        HiCodeAssistantBundle.get("dialog.tokenLimitExceeded.description"))
                .yesText(HiCodeAssistantBundle.get("dialog.continue"))
                .noText(HiCodeAssistantBundle.get("dialog.cancel"))
                .icon(HiCodeAssistantIcons.SYSTEM_ICON)
                .doNotAsk(new DoNotAskOption.Adapter() {
                    @Override
                    public void rememberChoice(boolean isSelected, int exitCode) {
                        if (isSelected) {
                            ConversationsState.getInstance().discardAllTokenLimits();
                        }
                    }

                    @NotNull
                    @Override
                    public String getDoNotShowMessage() {
                        return ExecutionBundle.message("don.t.ask.again");
                    }

                    @Override
                    public boolean shouldSaveOptionsOnCancel() {
                        return true;
                    }
                })
                .guessWindowAndAsk() ? OK : CANCEL;
    }

    public static void showSelectedEditorSelectionWarning(AnActionEvent event) {
        var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
        locationOnScreen.y = locationOnScreen.y - 16;
        showSelectedEditorSelectionWarning(requireNonNull(event.getProject()), locationOnScreen);
    }

    public static void showSelectedEditorSelectionWarning(
            @NotNull Project project,
            Point locationOnScreen) {
        showWarningBalloon(
                EditorUtil.getSelectedEditor(project) == null
                        ? HiCodeAssistantBundle.get("ui.overlay.warn1")
                        : HiCodeAssistantBundle.get("ui.overlay.warn2"),
                locationOnScreen);
    }

    public static void showWarningBalloon(String content, Point locationOnScreen) {
        showBalloon(content, MessageType.WARNING, locationOnScreen);
    }

    public static void showInfoBalloon(String content, Point locationOnScreen) {
        showBalloon(content, MessageType.INFO, locationOnScreen);
    }

    private static void showBalloon(String content, MessageType messageType, Point locationOnScreen) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(content, messageType, null)
                .setFadeoutTime(2500)
                .createBalloon()
                .show(RelativePoint.fromScreen(locationOnScreen), Balloon.Position.above);
    }

    public static void showBalloon(String content, MessageType messageType, JComponent component) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(content, messageType, null)
                .setFadeoutTime(2500)
                .createBalloon()
                .show(RelativePoint.getSouthOf(component), Balloon.Position.below);
    }
}
