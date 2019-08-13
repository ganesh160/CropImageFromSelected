package com.example.cropimagefromselected

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.example.cropimagefromselected.cropImages.ImagePickerActivity
import com.example.cropimagefromselected.cropImages.ImagePickerKotlin
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception


class MainActivity : AppCompatActivity()
{

    var IMAGE_PICK_CODE:Int=1000

    internal var image: String? = null
    internal var image_url: String? = null
    internal var bit: Bitmap? = null

    internal var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_image_btn.setOnClickListener { v->

            //val intent = Intent(Intent.ACTION_PICK)
            //intent.type = "image/*"
            //startActivityForResult(intent, IMAGE_PICK_CODE)

            startActivityForResult(Intent(this, ImagePickerKotlin::class.java), IMAGE_PICK_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try{

            if (resultCode == Activity.RESULT_OK){
                when (requestCode){

                    IMAGE_PICK_CODE -> {


                        imagePath = data!!.getStringExtra("image_path")
                        bit = getImageFromStorage(imagePath!!)
                        mimage_view.setImageBitmap(bit)
                        mimage_view.setVisibility(View.VISIBLE)
                        mimage_view.setBackgroundResource(0)
                        val bout = ByteArrayOutputStream()
                        bit!!.compress(Bitmap.CompressFormat.JPEG, 100, bout)
                        val img = bout.toByteArray()
                        image = Base64.encodeToString(img, Base64.DEFAULT)
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,""+e,Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageFromStorage( path :String) : Bitmap? {

       try {
           val f = File(path)
           // First decode with inJustDecodeBounds=true to check dimensions
           val options = BitmapFactory.Options()
           options.inJustDecodeBounds = false
           // Calculate inSampleSize
           options.inSampleSize = calculateInSampleSize(options, 1012, 1012)

           return BitmapFactory.decodeStream(FileInputStream(f), null, options)

       }catch (e:Exception){

       }
        return null
    }

    private fun calculateInSampleSize(options:BitmapFactory.Options , reqWidth:Int, reqHeight:Int ) : Int{
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (height / inSampleSize > reqHeight && width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}
