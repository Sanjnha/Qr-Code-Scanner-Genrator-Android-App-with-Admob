package wasakey.w3apps.com.qrcode.helpers.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import wasakey.w3apps.com.qrcode.QRCobaApplication;

public class PermissionUtil {
    public static final int REQUEST_CODE_PERMISSION_DEFAULT = 1;
    private static PermissionUtil sInstance;

    private PermissionUtil() {

    }

    public static PermissionUtil on() {
        if (sInstance == null) {
            sInstance = new PermissionUtil();
        }

        return sInstance;
    }

    public synchronized boolean requestPermission(Activity activity, String... permissions) {
        return requestPermission(null, activity,
                REQUEST_CODE_PERMISSION_DEFAULT, Arrays.asList(permissions));
    }

    public synchronized boolean requestPermission(Fragment fragment, String... permissions) {
        return requestPermission(fragment, null, REQUEST_CODE_PERMISSION_DEFAULT, Arrays.asList(permissions));
    }

    public synchronized boolean requestPermission(Activity activity, int requestCode,
                                                  String... permissions) {
        return requestPermission(null, activity, requestCode, Arrays.asList(permissions));
    }

    public synchronized boolean requestPermission(Fragment fragment, int requestCode, String... permissions) {
        return requestPermission(fragment, null, requestCode, Arrays.asList(permissions));
    }

    private boolean requestPermission(Fragment fragment, Activity activity,
                                      int requestCode, List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> permissionsNotTaken = new ArrayList<>();

        for (int i = 0; i < permissions.size(); i++) {
            if (!isAllowed(permissions.get(i))) {
                permissionsNotTaken.add(permissions.get(i));
            }
        }

        if (permissionsNotTaken.isEmpty()) {
            return true;
        }

        if (fragment == null) {
            activity.requestPermissions(permissionsNotTaken.toArray(new String[permissionsNotTaken.size()]), requestCode);
        } else {
            fragment.requestPermissions(permissionsNotTaken.toArray(new String[permissionsNotTaken.size()]), requestCode);
        }

        return false;
    }

    boolean isAllowed(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return QRCobaApplication.getContext().checkSelfPermission(permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
