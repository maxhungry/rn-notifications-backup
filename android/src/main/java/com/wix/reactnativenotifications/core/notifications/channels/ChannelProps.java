package com.wix.reactnativenotifications.core.notifications.channels;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ChannelProps {
    static final String BYPASS_DO_NOT_DISTURB = "bypassDoNotDisturb"; // Read-only
    static final String DESCRIPTION = "description";
    static final String GROUP = "group"; // Read-only after creation
    static final String IMPORTANCE = "importance"; // Read-only after creation
    static final String LIGHTS_COLOR = "lightsColor"; // Read-only after creation
    static final String LOCKSCREEN_VISIBILITY = "lockscreenVisibility"; // Read-only
    static final String NAME = "name";
    static final String SHOW_BADGE = "showBadge"; // Read-only after creation
    static final String SOUND = "sound"; // Read-only after creation
    static final String SOUND_CONTENT_TYPE = "soundContentType"; // Write-only during creation
    static final String SOUND_USAGE = "soundUsage"; // Write-only during creation
    static final String VIBRATION_PATTERN = "vibrationPattern"; // Read-only after creation

    public static ChannelProps fromBundle(Context context, Bundle bundle) {
        return new ChannelProps(context, new Bundle(bundle));
    }

    private Context mContext;
    private Bundle mProperties;

    ChannelProps(Context context, Bundle properties) {
        mContext = context;
        mProperties = properties;
    }

    @Nullable
    public Boolean getBypassDoNotDisturb() {
        return getBoolean(BYPASS_DO_NOT_DISTURB);
    }

    @Nullable
    public String getDescription() {
        return mProperties.getString(DESCRIPTION);
    }

    @Nullable
    public String getGroup() {
        return mProperties.getString(GROUP);
    }

    @Nullable
    public Integer getImportance() {
        return getInteger(IMPORTANCE);
    }

    @Nullable
    public Integer getLightsColor() {
        return colorFromString(mProperties.getString(LIGHTS_COLOR));
    }

    @Nullable
    public Integer getLockscreenVisibility() {
        return getInteger(LOCKSCREEN_VISIBILITY);
    }

    @Nullable
    public String getName() {
        return mProperties.getString(NAME);
    }

    @Nullable
    public Boolean getShowBadge() {
        return getBoolean(SHOW_BADGE);
    }

    @Nullable
    public Uri getSound() {
        return uriFromString(mProperties.getString(SOUND));
    }

    @Nullable
    public Integer getSoundContentType() {
        return getInteger(SOUND_CONTENT_TYPE);
    }

    @Nullable
    public Integer getSoundUsage() {
        return getInteger(SOUND_USAGE);
    }

    @Nullable
    public long[] getVibrationPattern() {
        return mProperties.getLongArray(VIBRATION_PATTERN);
    }

    @NonNull
    public Bundle asBundle() {
        return new Bundle(mProperties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        for (String key : mProperties.keySet()) {
            sb.append(key).append("=").append(mProperties.get(key)).append(", ");
        }
        return sb.toString();
    }

    @Nullable
    private Boolean getBoolean(String key) {
        final Object object = mProperties.get(key);

        if (object instanceof Boolean) {
            return (Boolean) object;
        }

        return booleanFromString(mProperties.getString(key));
    }

    @Nullable
    private Integer getInteger(String key) {
        final Object object = mProperties.get(key);

        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        return integerFromString(mProperties.getString(key));
    }

    @Nullable
    private Boolean booleanFromString(String string) {
        if (string != null) {
            try {
                return Boolean.parseBoolean(string);
            } catch (NumberFormatException e) {
                // Move on
            }
        }

        return null;
    }

    @Nullable
    private Integer integerFromString(String string) {
        if (string != null) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                // Move on
            }
        }

        return null;
    }

    @Nullable
    private Integer colorFromString(String string) {
        if (string != null) {
            try {
                return Color.parseColor(string);
            } catch (IllegalArgumentException e) {
                // Move on
            }
        }

        return null;
    }

    @Nullable
    private Uri uriFromString(String string) {
        if (string != null) {
            return Uri.parse(string.indexOf("://") > 0 ? string : "android.resource://" + mContext.getPackageName() + "/raw/" + string);
        }

        return null;
    }
}
