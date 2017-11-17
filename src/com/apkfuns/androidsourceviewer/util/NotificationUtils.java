package com.apkfuns.androidsourceviewer.util;

import com.apkfuns.androidsourceviewer.entity.Constant;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;
import java.net.URL;

public class NotificationUtils {

    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.balloonGroup(Constant.TITLE);

    /**
     * show a Notification
     *
     * @param message
     * @param type
     */
    public static void showNotification(final String message, final NotificationType type,
                                        @Nullable final NotificationUrlClickListener listener) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Notification notification =
                        NOTIFICATION_GROUP.createNotification(Constant.TITLE,
                                message == null || message.trim().length() == 0 ? "[Empty]" : message,
                                type, new NotificationListener() {
                                    @Override
                                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
                                        URL url = hyperlinkEvent.getURL();
                                        if (url == null) {
                                            return;
                                        }
                                        if (listener != null) {
                                            listener.onUrlClick(url);
                                            return;
                                        }
                                        if ("file".equals(url.getProtocol())) {
                                            if ("open".equals(url.getHost())) {
                                                String[] args = url.getQuery().split("=");
                                                if (args.length == 2) {
                                                    Utils.openDirectory(args[1]);
                                                }
                                            }
                                        }
                                    }
                                });
                Notifications.Bus.notify(notification);
            }
        });
    }

    /**
     * show a error Notification
     *
     * @param message
     */
    public static void errorNotification(final String message) {
        showNotification(message, NotificationType.ERROR, null);
    }

    /**
     * show a info Notification
     *
     * @param message
     */
    public static void infoNotification(final String message) {
        showNotification(message, NotificationType.INFORMATION, null);
    }

    public static void infoNotification(String message, NotificationUrlClickListener clickListener) {
        showNotification(message, NotificationType.INFORMATION, clickListener);
    }

    /**
     * 不会消失的弹出框
     *
     * @param message
     */
    public static void showMessageDialog(final String message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Messages.showMessageDialog(message, "Error", Messages.getInformationIcon());
            }
        });
    }

    /**
     * error message dialog
     *
     * @param message
     */
    public static void errorMsgDialog(String message) {
        Messages.showMessageDialog(message, "Error", Messages.getInformationIcon());
    }

    public interface NotificationUrlClickListener {
        void onUrlClick(URL url);
    }

}
