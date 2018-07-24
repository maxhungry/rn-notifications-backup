package com.wix.reactnativenotifications.core.notifications.styles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public interface ILocalNotificationStyle {

    public static final String NAME = "name";

    /**
     * Apply this style to builder in accordance with the provided properties.
     *
     * @param context Android context.
     * @param builder Notification builder where the style should be applied.
     * @param properties Style-specific properties which may customize the applied style.
     */
    void applyStyle(Context context, NotificationCompat.Builder builder, Bundle properties, OnStylingCompleteCallback callback);

    public interface OnStylingCompleteCallback {
    /**
     * Called when a style has finished been applied, successfully or otherwise. Any loaded resources (e.g. Bitmap) are
     * only valid for the duration of this callback.
     *
     * @param context The Android context that was provided to applyStyle.
     * @param builder The notification builder that was provided to applyStyle.
     * @param properties Style-specific properties that were provided to applyStyle.
     * @param applied true if a style was applied to the builder, false otherwise (i.e. properties were invalid).
     */
        void onStylingComplete(Context context, NotificationCompat.Builder builder, Bundle properties, boolean applied);
    }
}
