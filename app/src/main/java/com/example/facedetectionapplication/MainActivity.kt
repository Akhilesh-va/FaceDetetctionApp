package com.example.facedetectionapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
       val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager)!=null){
                startActivityForResult( intent,123)
            }else{
                Toast.makeText(this, "Unable to use camera ", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectface(bitmap) // Pass non-null Bitmap
            } else {
                Toast.makeText(this, "Unable to retrieve image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun detectface(bitmap: Bitmap?) {
        if (bitmap == null) {
            // Added a null check to handle null values for the bitmap parameter
            Toast.makeText(this, "Bitmap is null", Toast.LENGTH_SHORT).show() // Change 3: Added early return for null bitmap
            return
        }
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully
                var resultText = ""
                var i=1
                for(face in faces){
                    resultText= " Face Number $i" +
                            "\nSmile : ${face.smilingProbability?.times(100)}%"+
                            "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}" +
                            "\nRight Eye Open : ${face.rightEyeOpenProbability?.times(100)}"
                    i++

                }
                if(faces.isEmpty()){
                    Toast.makeText(this, "No face detetcted", Toast.LENGTH_SHORT).show()
                }
                else{
                    showCustomToast(resultText)
                }
                
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show()
            }


    }
    private fun showCustomToast(message: String) {
        val toast = Toast(this)
        val textView = TextView(this).apply {
            text = message
            setPadding(16, 16, 16, 16)
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.BLACK)
        }
        toast.view = textView
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

}