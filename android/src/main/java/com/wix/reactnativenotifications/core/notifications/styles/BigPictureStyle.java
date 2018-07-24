package com.wix.reactnativenotifications.core.notifications.styles;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.wix.reactnativenotifications.core.BitmapLoader;

public class BigPictureStyle implements ILocalNotificationStyle {

    public static final String STYLE_NAME = "bigPicture";

    public static final String BIG_CONTENT_TITLE = "bigContentTitle";
    public static final String SUMMARY_TEXT = "summaryText";
    public static final String BIG_LARGE_ICON = "bigLargeIcon";
    public static final String BIG_PICTURE = "bigPicture";

    @Override
    public void applyStyle(Context context, NotificationCompat.Builder builder, Bundle properties, OnStylingCompleteCallback callback) {
        new Loader(context, builder, properties, callback).apply();
    }

    private static final class Loader {
        private final Context context;
        private final NotificationCompat.Builder builder;
        private final Bundle properties;
        private final OnStylingCompleteCallback callback;

        private final NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();

        private Bitmap largeIconBitmap;
        private Bitmap bigPictureBitmap;

        private BitmapLoader largeIconBitmapLoader;
        private BitmapLoader bigPictureBitmapLoader;

        Loader(Context context, NotificationCompat.Builder builder, Bundle properties, OnStylingCompleteCallback callback) {
            this.context = context;
            this.builder = builder;
            this.properties = properties;
            this.callback = callback;
        }

        public void apply() {
            bigPictureStyle.setBigContentTitle(properties.getString(BIG_CONTENT_TITLE));
            bigPictureStyle.setSummaryText(properties.getString(SUMMARY_TEXT));

            final String largeIcon = properties.getString(BIG_LARGE_ICON);
            final String bigPicture = properties.getString(BIG_PICTURE);

            largeIconBitmapLoader = new BitmapLoader(context);
            bigPictureBitmapLoader = new BitmapLoader(context);

            largeIconBitmapLoader.loadImage(largeIcon, new BitmapLoader.OnBitmapLoadedCallback() {
                @Override
                public void onBitmapLoaded(@Nullable Bitmap bitmap) {
                    if (bitmap != null) {
                        largeIconBitmap = bitmap.copy(bitmap.getConfig(), false);
                        bigPictureStyle.bigLargeIcon(largeIconBitmap);
                    }

                    largeIconBitmapLoader = null;

                    if (bigPictureBitmapLoader == null) {
                        done();
                    }
                }
            });

            bigPictureBitmapLoader.loadImage(bigPicture, new BitmapLoader.OnBitmapLoadedCallback() {
                @Override
                public void onBitmapLoaded(@Nullable Bitmap bitmap) {
                    if (bitmap != null) {
                        bigPictureBitmap = bitmap.copy(bitmap.getConfig(), false);
                        bigPictureStyle.bigPicture(bigPictureBitmap);
                    }

                    bigPictureBitmapLoader = null;

                    if (largeIconBitmapLoader == null) {
                        done();
                    }
                }
            });
        }

        private void done() {
            builder.setStyle(bigPictureStyle);
            callback.onStylingComplete(context, builder, properties, true);

            if (largeIconBitmap != null) {
                largeIconBitmap.recycle();
            }

            if (bigPictureBitmap != null) {
                bigPictureBitmap.recycle();
            }
        }
    }
}
