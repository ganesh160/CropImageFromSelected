package com.example.cropimagefromselected.cropImages;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.cropimagefromselected.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 2365;
    private static final int REQUEST_CROP_IMAGE = 2342;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_image_picker);


        startActivityForResult(getPickImageChooserIntent(),REQUEST_PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_PICK_IMAGE)
            {
                Intent intent = new Intent(this, CropImageActivty.class);
                Uri imageUri = getPickImageResultUri(data);
                intent.putExtra(CropImageActivty.EXTRA_IMAGE_URI, imageUri.toString());
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
            }
            else if(requestCode == REQUEST_CROP_IMAGE)
            {
                System.out.println("Image crop success :"+data.getStringExtra(CropImageActivty.CROPPED_IMAGE_PATH));
                String imagePath = new File(data.getStringExtra(CropImageActivty.CROPPED_IMAGE_PATH), "image.jpg").getAbsolutePath();
                Intent result = new Intent();
                result.putExtra("image_path", imagePath);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        }
        else
        {
            //System.out.println("Image crop failed");
            Toast.makeText(this, "Image crop failed", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    public Intent getPickImageChooserIntent()
    {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam)
        {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery)
        {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list, so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents)
        {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))
            {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri()
    {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null)
        {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }


    public Uri getPickImageResultUri(Intent data)
    {
        boolean isCamera = true;
        if (data != null)
        {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);

        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }
}

