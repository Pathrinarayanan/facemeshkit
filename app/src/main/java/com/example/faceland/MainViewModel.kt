package com.google.mediapipe.examples.facelandmarker
/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *  This ViewModel is used to store face landmarker helper settings
 */
class MainViewModel : ViewModel() {

    private var _delegate: Int = FaceLandmarkerHelper.DELEGATE_CPU
    private var _minFaceDetectionConfidence: Float =
        FaceLandmarkerHelper.DEFAULT_FACE_DETECTION_CONFIDENCE
    private var _minFaceTrackingConfidence: Float = FaceLandmarkerHelper
        .DEFAULT_FACE_TRACKING_CONFIDENCE
    private var _minFacePresenceConfidence: Float = FaceLandmarkerHelper
        .DEFAULT_FACE_PRESENCE_CONFIDENCE
    private var _maxFaces: Int = FaceLandmarkerHelper.DEFAULT_NUM_FACES
    val outputImage = MutableLiveData<Bitmap?>()

    // Method to remove background from an image resource
    suspend fun removeBackgroundAndSave(context: Application, imageUrl: String) {
        withContext(Dispatchers.IO) {
            // Download the image from the URL
            val originalBitmap = downloadImageFromUrl(imageUrl)

            if (originalBitmap != null) {
                // Use RemoveBg API to remove the background
                val remover = RemoveBg(context)
                val backgroundRemovedBitmap = remover.clearBackground(originalBitmap)

                // Collect the result synchronously
                backgroundRemovedBitmap.collect { processedBitmap ->
                    processedBitmap?.let {
                        val savedFile = saveBitmapToStorage(context, it)
                        // Post the result to the LiveData
                        withContext(Dispatchers.Main) {
                            outputImage.postValue(it)
                        }
                        Log.d("RemoveBg", "Saved file path: ${savedFile?.absolutePath}")
                    }
                }
            } else {
                Log.e("RemoveBg", "Failed to download image from URL.")
            }
        }
    }

    private fun downloadImageFromUrl(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // Convert drawable resource to Bitmap
    private fun getBitmapFromDrawable(context: Application, drawableResId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, drawableResId)
    }

    // Save the bitmap to internal storage
    private fun saveBitmapToStorage(context: Application, bitmap: Bitmap): File {
        val filename = "processed_image.png"
        val file = File(context.filesDir, filename)


        // Share the file path globally

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            Log.d("ImageSaved", "Saved at: ${file.absolutePath}")
            GlobalImagePath.filePath = file.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("pathri","not + "+ e.toString())

        }
        return file
    }
    val currentDelegate: Int get() = _delegate
    val currentMinFaceDetectionConfidence: Float
        get() =
            _minFaceDetectionConfidence
    val currentMinFaceTrackingConfidence: Float
        get() =
            _minFaceTrackingConfidence
    val currentMinFacePresenceConfidence: Float
        get() =
            _minFacePresenceConfidence
    val currentMaxFaces: Int get() = _maxFaces

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinFaceDetectionConfidence(confidence: Float) {
        _minFaceDetectionConfidence = confidence
    }
    fun setMinFaceTrackingConfidence(confidence: Float) {
        _minFaceTrackingConfidence = confidence
    }
    fun setMinFacePresenceConfidence(confidence: Float) {
        _minFacePresenceConfidence = confidence
    }

    fun setMaxFaces(maxResults: Int) {
        _maxFaces = maxResults
    }
    object GlobalImagePath {
        var filePath: String? = null
    }

}