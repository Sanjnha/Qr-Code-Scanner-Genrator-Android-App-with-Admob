package wasakey.w3apps.com.qrcode.helpers.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import wasakey.w3apps.com.qrcode.databinding.ProgresssDialogLayoutBinding;

public class ProgressDialogUtil {

    private static ProgressDialogUtil sInstance;
    private AlertDialog mAlertDialog;

    private ProgressDialogUtil() {

    }

    public static ProgressDialogUtil on() {
        if (sInstance == null) {
            sInstance = new ProgressDialogUtil();
        }

        return sInstance;
    }

    public void showProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        ProgresssDialogLayoutBinding binding =
                ProgresssDialogLayoutBinding.inflate(LayoutInflater.from(context),
                        null, false);

        binding.textViewMessage.setTypeface(null, Typeface.NORMAL);

        builder.setCancelable(false);
        builder.setView(binding.getRoot());

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void hideProgressDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
