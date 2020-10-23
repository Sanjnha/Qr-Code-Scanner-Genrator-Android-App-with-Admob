package wasakey.w3apps.com.qrcode.helpers.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefUtil {
    private static SharedPreferences preferences;

    private SharedPrefUtil() {
    }

    public static void init(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static boolean write(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean write(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean write(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static boolean write(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static String readString(String key) {
        return preferences.getString(key, "");
    }

    public static long readLong(String key) {
        return preferences.getLong(key, 0);
    }

    public static int readInt(String key) {
        return preferences.getInt(key, 0);
    }

    public static boolean readBooleanDefaultTrue(String key) {
        return preferences.getBoolean(key, true);
    }

    public static boolean readBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static boolean contains(String key) {
        return preferences.contains(key);
    }

    public static void clear() {
        preferences.edit().clear().apply();
    }

    public static void delete(String key) {
        preferences.edit().remove(key).apply();
    }
}