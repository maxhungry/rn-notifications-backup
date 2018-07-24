package com.wix.reactnativenotifications.core.notifications.styles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class InboxStyle implements ILocalNotificationStyle {

    public static final String STYLE_NAME = "inbox";

    public static final String BIG_CONTENT_TITLE = "bigContentTitle";
    public static final String SUMMARY_TEXT = "summaryText";
    public static final String LINES = "lines";

    @Override
    public void applyStyle(Context context, NotificationCompat.Builder builder, Bundle properties, OnStylingCompleteCallback callback) {
        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        final ArrayList<String> lines = properties.getStringArrayList(LINES);

        inboxStyle.setBigContentTitle(properties.getString(BIG_CONTENT_TITLE));
        inboxStyle.setSummaryText(properties.getString(SUMMARY_TEXT));

        if (lines != null) {
            for (final String line : lines) {
                inboxStyle.addLine(line);
            }
        }

        builder.setStyle(inboxStyle);
        callback.onStylingComplete(context, builder, properties, true);
    }
}
