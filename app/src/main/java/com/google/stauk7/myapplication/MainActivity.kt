package com.google.stauk7.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import java.io.IOException
import android.text.method.ScrollingMovementMethod




class MainActivity : AppCompatActivity() {
    companion object {
        // We only need to analyze the part of the image that has text, so we set crop percentages
        // to avoid analyze the entire image from the live camera feed.
        const val DESIRED_WIDTH_CROP_PERCENT = 8
        const val DESIRED_HEIGHT_CROP_PERCENT = 74
    }

    val REQUEST_PHOTO_PICKER_SINGLE_SELECT = 1

    // When using Japanese script library
    val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

    // layout
    lateinit var image: ImageView
    lateinit var importButton: Button
    lateinit var recoButton: Button
    lateinit var resultText: TextView

    var currentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLayout()


    }

    private fun initLayout() {
        image = findViewById(R.id.image)
        importButton = findViewById(R.id.import_button)
        recoButton = findViewById(R.id.reco_button)
        resultText = findViewById(R.id.resut_text)
        resultText.movementMethod = ScrollingMovementMethod()

        importButton.setOnClickListener { importImage() }
        recoButton.setOnClickListener {
            if (currentUri != null) {
                textRecognition()
            } else {
                Snackbar.make(it, "画像がありません", Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }

    private fun importImage() {
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        startActivityForResult(intent, REQUEST_PHOTO_PICKER_SINGLE_SELECT)
    }

    // onActivityResult() handles callbacks from the photo picker.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            // Handle error
            return
        }
        when (requestCode) {
            REQUEST_PHOTO_PICKER_SINGLE_SELECT -> {
                // Get photo picker response for single select.
                currentUri = data?.data
                // Do stuff with the photo/video URI.
                image.setImageURI(currentUri)
                return
            }
        }
    }

    private fun textRecognition() {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(this, currentUri!!)
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    val resultText = result.text
                    Log.d("result text", resultText)
                    this.resultText.text = resultText
                }.addOnFailureListener { e -> e.printStackTrace() }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}