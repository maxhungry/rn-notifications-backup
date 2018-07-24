package com.wix.reactnativenotifications.core.notifications.styles;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class MessagingStyle implements ILocalNotificationStyle {

    public static final String STYLE_NAME = "messaging";

    public static final String CONVERSATION_TITLE = "conversationTitle";
    public static final String MESSAGES = "messages";
    public static final String USER_DISPLAY_NAME = "userDisplayName";

    public static final String MESSAGE_TEXT = "text";
    public static final String MESSAGE_TIMESTAMP = "timestamp"; // ms since Unix epoch.
    public static final String MESSAGE_SENDER = "sender";

    @Override
    public void applyStyle(Context context, NotificationCompat.Builder builder, Bundle properties, OnStylingCompleteCallback callback) {
        final String userDisplayName = properties.getString(USER_DISPLAY_NAME);

        if (userDisplayName == null) {
            Log.e(LOGTAG, USER_DISPLAY_NAME + " is required when using the " + STYLE_NAME + " notification style");
            callback.onStylingComplete(context, builder, properties, false);
            return;
        }

        final NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(userDisplayName);
        final ArrayList<Bundle> messages = properties.getParcelableArrayList(MESSAGES);

        if (messages != null) {
            for (final Bundle message : messages) {
                final String text = message.getString(MESSAGE_TEXT);
                final Object timestamp = message.containsKey(MESSAGE_TIMESTAMP) ? message.get(MESSAGE_TIMESTAMP) : null;
                final String sender = message.getString(MESSAGE_SENDER);

                if (text != null && timestamp instanceof Number && sender != null) {
                    messagingStyle.addMessage(text, ((Number) timestamp).longValue(), sender);
                } else {
                    Log.e(LOGTAG, MESSAGE_TEXT + ", " + MESSAGE_TIMESTAMP + " and " + MESSAGE_SENDER + " are all required fields for " + MESSAGES + " when using the " + STYLE_NAME + " notification style");
                    callback.onStylingComplete(context, builder, properties, false);
                    return;
                }
            }
        }

        messagingStyle.setConversationTitle(properties.getString(CONVERSATION_TITLE));

        builder.setStyle(messagingStyle);
        callback.onStylingComplete(context, builder, properties, true);
    }
}
