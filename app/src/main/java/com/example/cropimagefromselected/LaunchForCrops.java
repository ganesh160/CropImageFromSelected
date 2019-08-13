package com.example.cropimagefromselected;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.cropimagefromselected.cropImages.ImagePickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LaunchForCrops extends AppCompatActivity {

    Button select_image_btn;
    ImageView mimage_view;
    public static final int IMAGE_PICK_CODE=1000;


    String image;
    String image_url;
    Bitmap bit;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_for_crops);
        select_image_btn=findViewById(R.id.select_image_btn);
        mimage_view=findViewById(R.id.mimage_view);
        select_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(LaunchForCrops.this, ImagePickerActivity.class), IMAGE_PICK_CODE);
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            if (resultCode == RESULT_OK)
            {
                switch (requestCode)
                {
                    case IMAGE_PICK_CODE:
                        imagePath = data.getStringExtra("image_path");
                        bit = getImageFromStorage(imagePath);
                        mimage_view.setImageBitmap(bit);
                        mimage_view.setVisibility(View.VISIBLE);
                        mimage_view.setBackgroundResource(0);
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        bit.compress(Bitmap.CompressFormat.JPEG, 100, bout);
                        byte img[] = bout.toByteArray();
                        image = Base64.encodeToString(img, Base64.DEFAULT);
                        break;
                }
            }
            else
            {
                System.out.println("Failed to load image");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getImageFromStorage(String path)
    {
        try
        {
            File f = new File(path);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 1012, 1012);

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            final int halfHeight = height ;
            final int halfWidth = width ;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
