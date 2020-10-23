package wasakey.w3apps.com.qrcode.helpers.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import wasakey.w3apps.com.qrcode.R;

public class FileUtil {
    public static File getEmptyFile(Context context, String fileNamePrefix,
                                    String fileNameBody, String fileNameSuffix,
                                    String directoryType) {

        if (isExternalStorageWritable()) {
            String fileName = fileNamePrefix + fileNameBody + fileNameSuffix;

            File storageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(directoryType),
                    context.getString(R.string.app_name));

            boolean isDirectoryCreated;

            if (!storageDirectory.exists()) {
                isDirectoryCreated = storageDirectory.mkdirs();
            } else {
                isDirectoryCreated = true;
            }

            File file;

            if (isDirectoryCreated) {
                try {
                    file = new File(storageDirectory, fileName);

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (IOException e) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        Log.e(FileUtil.class.getSimpleName(), e.getMessage());
                    }

                    return null;
                }
            } else {
                return null;
            }

            return file;
        } else {
            return null;
        }
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
