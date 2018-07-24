package com.wix.reactnativenotifications.core.notifications.styles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class BigTextStyle implements ILocalNotificationStyle {

    public static final String STYLE_NAME = "bigText";

    public static final String BIG_TEXT = "bigText";
    public static final String BIG_CONTENT_TITLE = "bigContentTitle";
    public static final String SUMMARY_TEXT = "summaryText";

    @Override
    public void applyStyle(Context context, NotificationCompat.Builder builder, Bundle properties, OnStylingCompleteCallback callback) {
        final NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        bigTextStyle.bigText(properties.getString(BIG_TEXT));
        bigTextStyle.setBigContentTitle(properties.getString(BIG_CONTENT_TITLE));
        bigTextStyle.setSummaryText(properties.getString(SUMMARY_TEXT));

        builder.setStyle(bigTextStyle);
        callback.onStylingComplete(context, builder, properties, true);
    }
}
