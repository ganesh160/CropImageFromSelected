package com.example.cropimagefromselected.cropImages

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.cropimagefromselected.R
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_crop_image_kotlin.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class CropImageKotlin : AppCompatActivity()  , CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener
{
    companion object{
        private val DEFAULT_ASPECT_RATIO_VALUES = 100

        val CROPPED_IMAGE_PATH = "cropped_image_path"
        val EXTRA_IMAGE_URI = "cropped_image_path"

        val FIXED_ASPECT_RATIO = "extra_fixed_aspect_ratio"
        val EXTRA_ASPECT_RATIO_X = "extra_aspect_ratio_x"
        val EXTRA_ASPECT_RATIO_Y = "extra_aspect_ratio_y"

        private val ASPECT_RATIO_X = "ASPECT_RATIO_X"

        private val ASPECT_RATIO_Y = "ASPECT_RATIO_Y"


        private var mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES

        private var mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES

        private var isFixedAspectRatio = false

        internal var croppedImage: Bitmap?= null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image_kotlin)

        if (!intent.hasExtra(EXTRA_IMAGE_URI)) {
            cropFailed()
            return
        }

        isFixedAspectRatio = intent.getBooleanExtra(FIXED_ASPECT_RATIO, false)
        mAspectRatioX = intent.getIntExtra(EXTRA_ASPECT_RATIO_X, DEFAULT_ASPECT_RATIO_VALUES)
        mAspectRatioY = intent.getIntExtra(EXTRA_ASPECT_RATIO_Y, DEFAULT_ASPECT_RATIO_VALUES)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        // Initialize components of the app
        // If you want to fix the aspect ratio, set it to 'true'
        mmCropImageView.setFixedAspectRatio(isFixedAspectRatio)

        if (savedInstanceState == null) {
            mmCropImageView.setImageUriAsync(imageUri)
        }
    }

    fun cropFailed(){

        Toast.makeText(this,"Image crop failed",Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX)
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY)
    }

    // Restores the state upon rotating the screen/restarting the activity

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X)
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y)
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_crop_image,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_crop) {
            mmCropImageView.getCroppedImageAsync(mmCropImageView.getCropShape(), 0, 0)
            return true
        } else if (id == R.id.action_cancel) {
            cropFailed()
            return false
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        mmCropImageView.setOnSetImageUriCompleteListener(this)
        mmCropImageView.setOnGetCroppedImageCompleteListener(this)
    }

    override fun onStop() {
        super.onStop()
        mmCropImageView.setOnSetImageUriCompleteListener(null)
        mmCropImageView.setOnGetCroppedImageCompleteListener(null)
    }


    override fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?) {
        if (error == null){

        }else{
            Toast.makeText(this,"unable to load inmage",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGetCroppedImageComplete(view: CropImageView?, bitmap: Bitmap?, error: Exception?) {
        if (error == null){

            croppedImage = bitmap
            try {
                val path = saveToInternalStorage(this, bitmap!!)
                val resultIntent = Intent()
                resultIntent.putExtra(CROPPED_IMAGE_PATH, path)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
                cropFailed()
            }

        }else{
            cropFailed()
        }
    }

    @Throws(IOException::class)
    private fun saveToInternalStorage(context: Context, bitmapImage: Bitmap): String {
        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "image.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            //Bitmap scaledBitmap = getCompressedBitmap(bitmapImage);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fos!!.close()
        }
        return directory.absolutePath
    }





}
