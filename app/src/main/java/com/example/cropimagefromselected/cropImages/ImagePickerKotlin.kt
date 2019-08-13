package com.example.cropimagefromselected.cropImages

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import com.example.cropimagefromselected.R
import java.io.File
import java.util.ArrayList

class ImagePickerKotlin : AppCompatActivity() {

    private val REQUEST_PICK_IMAGE = 2365
    private val REQUEST_CROP_IMAGE = 2342

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_image_picker_kotlin)


        startActivityForResult(getPickImageChooserIntent(),REQUEST_PICK_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK){
            when (requestCode){

                REQUEST_PICK_IMAGE ->{
                    val intent = Intent(this, CropImageKotlin::class.java)
                    val imageUri = getPickImageResultUri(data)
                    intent.putExtra(CropImageKotlin.EXTRA_IMAGE_URI, imageUri.toString())
                    startActivityForResult(intent, REQUEST_CROP_IMAGE)
                }
                REQUEST_CROP_IMAGE ->{
                    val imagePath = File(data!!.getStringExtra(CropImageKotlin.CROPPED_IMAGE_PATH), "image.jpg").absolutePath
                    val result = Intent()
                    result.putExtra("image_path", imagePath)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                }

            }
        }
    }


    fun getPickImageChooserIntent() : Intent{

        // Determine Uri of camera image to save.
        val outputFileUri = getCaptureImageOutputUri()

        val allIntents = ArrayList<Intent>()
        val packageManager = packageManager

        // collect all camera intents
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }

        // the main intent is the last in the list, so pickup the useless one
        var mainIntent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())

        return chooserIntent
    }


    fun getCaptureImageOutputUri() : Uri? {
        var outputFileUri: Uri? = null
        val getImage = externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }


    fun getPickImageResultUri(data: Intent?) : Uri?{

        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE

        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.getData()
    }

}
