package wasakey.w3apps.com.qrcode.helpers.util.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;


public class ImagePicker {

    // Constants
    public final static int REQUEST_CODE_PICK_IMAGE = 15913;
    private final static int DEFAULT_MIN_WIDTH_QUALITY = 400; // Minimum pixels
    private final static String PICKER_TITLE = "Pick Image";

    private ImagePicker() {
        // Do nothing
    }

    /**
     * This method picks image from gallery, photos and camera of device
     *
     * @param activity current activity
     */
    public synchronized static void pickImage(Activity activity) {
        if (activity != null) {
            Intent imagePickingIntent = getImagePickingIntent(activity);

            if (imagePickingIntent != null) {
                activity.startActivityForResult(imagePickingIntent, REQUEST_CODE_PICK_IMAGE);
            }
        }
    }

    /**
     * This method picks image from gallery, photos and camera of device
     *
     * @param fragment current fragment
     */
    public synchronized static void pickImage(Fragment fragment) {
        if (fragment != null && fragment.getContext() != null) {
            Intent imagePickingIntent = getImagePickingIntent(fragment.getContext());

            if (imagePickingIntent != null) {
                fragment.startActivityForResult(imagePickingIntent, REQUEST_CODE_PICK_IMAGE);
            }
        }
    }

    /**
     * This method provides the intent to pick images from device gallery, camera and photos
     *
     * @param context current activity UI context
     */
    public static Intent getImagePickingIntent(Context context) {
        if (context == null) return null;

        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickPhotoIntent = new Intent();
        pickPhotoIntent.setType("image/*");
        pickPhotoIntent.setAction(Intent.ACTION_GET_CONTENT);

        intentList = addIntentsToList(context, intentList, pickPhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent =
                    Intent.createChooser(intentList.remove(intentList.size() - 1), PICKER_TITLE);
            chooserIntent
                    .putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    /**
     * This method provides information of an image
     *
     * @param context         current activity UI context
     * @param resultCode      resultCode according to the result
     * @param intentWithImage intent that contains the desired image
     */
    public static ImageInfo getPickedImageInfo(Context context, int resultCode, Intent intentWithImage) {
        if (context == null || resultCode != Activity.RESULT_OK) {
            return null;
        }

        return new ImageInfo(intentWithImage.getData(), false);
    }

    /**
     * This method provides bitmap of an image
     *
     * @param context         current activity UI context
     * @param resultCode      resultCode according to the result
     * @param intentWithImage intent that contains the desired image
     */
    public static Bitmap getPickedImageFromResult(Context context, int resultCode, Intent intentWithImage) {
        if (context == null || resultCode != Activity.RESULT_OK) {
            return null;
        }

        Log.d("Result Code", String.valueOf(resultCode));

        ImageInfo pickedImageInfo = getPickedImageInfo(context, resultCode, intentWithImage);
        if (pickedImageInfo.getImageUri() == null) {
            return null;
        }

        Log.d("Selected Image", pickedImageInfo.getImageUri().getPath());

        Bitmap bitmap = getImageResized(context, pickedImageInfo.getImageUri());
        int rotation = getRotation(context, pickedImageInfo.getImageUri());
        bitmap = rotate(bitmap, rotation);

        return bitmap;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> intentList, Intent intent) {
        if (context == null || intent == null) return intentList;

        List<ResolveInfo> resolveInfoList =
                context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;

            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);

            intentList.add(targetedIntent);
            Log.d("Intent", "" + intent.getAction() + " package: " + packageName);
        }

        return intentList;
    }

    /*
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    private static Bitmap getImageResized(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }

        Bitmap bitmap;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bitmap = decodeBitmap(context, imageUri, sampleSizes[i]);
            Log.d("Resizer", "new bitmap width = " + bitmap.getWidth());
            i++;
        } while (bitmap.getWidth() < DEFAULT_MIN_WIDTH_QUALITY && i < sampleSizes.length);

        return bitmap;
    }

    private static Bitmap decodeBitmap(Context context, Uri imageUri, int sampleSize) {
        if (context == null || imageUri == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(imageUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = null;

        if (fileDescriptor != null) {
            actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor.getFileDescriptor(), null, options);

            Log.d("Decode Bitmap", options.inSampleSize + " sample method bitmap ... " +
                    actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());
        }

        return actuallyUsableBitmap;
    }

    private static int getRotation(Context context, Uri imageUri) {
        int rotation = getRotationFromGallery(context, imageUri);
        Log.d("Image rotation", String.valueOf(rotation));
        return rotation;
    }

    private static int getRotationFromGallery(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return 0;
        }

        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        try (Cursor cursor = context.getContentResolver().query(imageUri, columns, null,
                null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            // Do nothing
        }

        return result;
    }

    private static Bitmap rotate(Bitmap bitmapIn, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            return Bitmap.createBitmap(bitmapIn, 0, 0,
                    bitmapIn.getWidth(), bitmapIn.getHeight(), matrix, true);
        }

        return bitmapIn;
    }
}
