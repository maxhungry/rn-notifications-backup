package com.wix.reactnativenotifications.core.notifications.styles;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;

public class LocalNotificationStyleManager {

    private static LocalNotificationStyleManager instance;

    public static synchronized void setInstance(LocalNotificationStyleManager instance) {
        LocalNotificationStyleManager.instance = instance;
    }

    public static synchronized LocalNotificationStyleManager getInstance() {
        if (instance == null) {
            instance = new LocalNotificationStyleManager();
        }
        return instance;
    }


    private HashMap<String, ILocalNotificationStyle> styles = new HashMap<String, ILocalNotificationStyle>();

    /*package*/ LocalNotificationStyleManager() {
    }

    public void registerDefaultStyles() {
        registerStyle(BigPictureStyle.STYLE_NAME, new BigPictureStyle());
        registerStyle(BigTextStyle.STYLE_NAME, new BigTextStyle());
        registerStyle(InboxStyle.STYLE_NAME, new InboxStyle());
        registerStyle(MessagingStyle.STYLE_NAME, new MessagingStyle());
    }

    @Nullable
    public synchronized ILocalNotificationStyle registerStyle(final String name, final ILocalNotificationStyle style) {
        return styles.put(name, style);
    }

    @Nullable
    public synchronized ILocalNotificationStyle deregisterStyle(final String name) {
        return styles.remove(name);
    }

    @Nullable
    public synchronized ILocalNotificationStyle getStyle(final String name) {
        return styles.get(name);
    }

    public void applyStyle(final Context context, final NotificationCompat.Builder builder, @Nullable final Bundle properties, ILocalNotificationStyle.OnStylingCompleteCallback callback) {
        final ILocalNotificationStyle style = properties != null ? getStyle(properties.getString(ILocalNotificationStyle.NAME, null)) : null;

        if (style != null) {
            style.applyStyle(context, builder, properties, callback);
        } else {
            callback.onStylingComplete(context, builder, properties, false);
        }
    }
}
