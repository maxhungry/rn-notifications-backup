package com.wix.reactnativenotifications;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.*;
import com.wix.reactnativenotifications.core.AppLifecycleFacade;
import com.wix.reactnativenotifications.core.AppLifecycleFacadeHolder;
import com.wix.reactnativenotifications.core.InitialNotificationHolder;
import com.wix.reactnativenotifications.core.JsIOHelper;
import com.wix.reactnativenotifications.core.ReactAppLifecycleFacade;
import com.wix.reactnativenotifications.core.notificationdrawer.INotificationDrawer;
import com.wix.reactnativenotifications.core.notificationdrawer.NotificationDrawer;
import com.wix.reactnativenotifications.core.notifications.ILocalNotification;
import com.wix.reactnativenotifications.core.notifications.LocalNotification;
import com.wix.reactnativenotifications.core.notifications.NotificationProps;
import com.wix.reactnativenotifications.core.notifications.channels.ChannelManager;
import com.wix.reactnativenotifications.core.notifications.channels.ChannelProps;
import com.wix.reactnativenotifications.fcm.FcmTokenService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class RNNotificationsModule extends ReactContextBaseJavaModule implements AppLifecycleFacade.AppVisibilityListener, Application.ActivityLifecycleCallbacks {

    public RNNotificationsModule(Application application, ReactApplicationContext reactContext) {
        super(reactContext);

        if (AppLifecycleFacadeHolder.get() instanceof ReactAppLifecycleFacade) {
            ((ReactAppLifecycleFacade) AppLifecycleFacadeHolder.get()).init(reactContext);
        }
        AppLifecycleFacadeHolder.get().addVisibilityListener(this);
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public String getName() {
        return "WixRNNotifications";
    }

    @Override
    public void initialize() {
        Log.d(LOGTAG, "Native module init");
        final INotificationDrawer notificationsDrawer = NotificationDrawer.get(getReactApplicationContext().getApplicationContext());
        notificationsDrawer.onAppInit();
    }

    @ReactMethod
    public void refreshToken() {
        Log.d(LOGTAG, "Native method invocation: refreshToken()");
        startTokenService(FcmTokenService.ACTION_REFRESH_TOKEN);
    }

    @ReactMethod
    public void invalidateToken() {
        Log.d(LOGTAG, "Native method invocation: invalidateToken()");
        startTokenService(FcmTokenService.ACTION_INVALIDATE_TOKEN);
    }

    @ReactMethod
    public void getInitialNotification(final Promise promise) {
        Log.d(LOGTAG, "Native method invocation: getInitialNotification");
        Object result = null;

        try {
            final NotificationProps notification = InitialNotificationHolder.getInstance().get();
            if (notification == null) {
                return;
            }

            result = Arguments.fromBundle(notification.asBundle());
        } finally {
            promise.resolve(result);
        }
    }

    @ReactMethod
    public void createChannel(String channelId, ReadableMap propsMap, Promise promise) {
        Log.d(LOGTAG, "Native method invocation: createChannel");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Context context = getReactApplicationContext().getApplicationContext();

            final ChannelProps channelProps = ChannelProps.fromBundle(context, Arguments.toBundle(propsMap));
            ChannelManager.createChannel(context, channelId, channelProps);

            final ChannelProps resultantChannelProps = ChannelManager.getChannelProps(context, channelId);
            promise.resolve(resultantChannelProps != null ? Arguments.fromBundle(resultantChannelProps.asBundle()) : null);
        } else {
            Log.e(LOGTAG, ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED);
            promise.reject(ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED);
        }
    }

    @ReactMethod
    public void getChannel(String channelId, Promise promise) {
        Log.d(LOGTAG, "Native method invocation: getChannel");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Context context = getReactApplicationContext().getApplicationContext();
            final ChannelProps channelProps = ChannelManager.getChannelProps(context, channelId);
            promise.resolve(channelProps != null ? channelProps.asBundle() : null);
        } else {
            Log.e(LOGTAG, ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED);
            promise.reject(ERROR_CODE_CHANNELS_NOT_SUPPORTED, ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED);
        }
    }

    @ReactMethod
    public void getChannels(Promise promise) {
        Log.d(LOGTAG, "Native method invocation: getChannels");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Context context = getReactApplicationContext().getApplicationContext();
            final Map<String, ChannelProps> allChannelProps = ChannelManager.getAllChannelProps(context);
            final WritableNativeMap writableMap = new WritableNativeMap();

            for (final Map.Entry<String, ChannelProps> channelProps : allChannelProps.entrySet()) {
                writableMap.putMap(channelProps.getKey(), Arguments.fromBundle(channelProps.getValue().asBundle()));
            }

            promise.resolve(writableMap);
        } else {
            Log.e(LOGTAG, ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED);
            promise.reject(ERROR_CODE_CHANNELS_NOT_SUPPORTED, ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED);
        }

    }

    @ReactMethod
    public void postLocalNotification(ReadableMap propsMap, int notificationId, String channelId) {
        Log.d(LOGTAG, "Native method invocation: postLocalNotification");
        final Context context = getReactApplicationContext().getApplicationContext();
        final NotificationProps localNotificationProps = NotificationProps.fromBundle(context, Arguments.toBundle(propsMap));
        final ILocalNotification notification = LocalNotification.get(context, localNotificationProps);
        notification.post(notificationId, channelId);
    }

    @ReactMethod
    public void cancelLocalNotification(int notificationId, @Nullable String notificationTag) {
        Log.d(LOGTAG, "Native method invocation: cancelLocalNotification");
        INotificationDrawer notificationsDrawer = NotificationDrawer.get(getReactApplicationContext().getApplicationContext());
        notificationsDrawer.onCancelLocalNotification(notificationTag, notificationId);
    }

    @ReactMethod
    public void cancelAllLocalNotifications() {
        Log.d(LOGTAG, "Native method invocation: cancelAllLocalNotifications");
        INotificationDrawer notificationDrawer = NotificationDrawer.get(getReactApplicationContext().getApplicationContext());
        notificationDrawer.onCancelAllLocalNotifications();
    }

    @ReactMethod
    public void consumeBackgroundQueue() {
        Log.d(LOGTAG, "Native method invocation: consumeBackgroundQueue");
        final JsIOHelper jsIOHelper = new JsIOHelper(getReactApplicationContext().getApplicationContext());
        jsIOHelper.consumeBackgroundQueue();
    }

    @Override
    public void onAppVisible() {
        final INotificationDrawer notificationsDrawer = NotificationDrawer.get(getReactApplicationContext().getApplicationContext());
        notificationsDrawer.onAppVisible();
    }

    @Override
    public void onAppNotVisible() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        final INotificationDrawer notificationsDrawer = NotificationDrawer.get(getReactApplicationContext().getApplicationContext());
        notificationsDrawer.onNewActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    protected void startTokenService(String action) {
        final Context appContext = getReactApplicationContext().getApplicationContext();
        final Intent intent = new Intent(appContext, FcmTokenService.class);
        intent.setAction(action);
        appContext.startService(intent);
    }

    private static final String ERROR_CODE_CHANNELS_NOT_SUPPORTED = "CHANNELS_NOT_SUPPORTED";
    private static final String ERROR_MESSAGE_CHANNELS_NOT_SUPPORTED = "Notification channels are only supported Android Oreo onwards.";
}
