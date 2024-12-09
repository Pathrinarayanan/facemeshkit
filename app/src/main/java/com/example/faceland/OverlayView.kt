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
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.faceland.R
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import dev.eren.removebg.RemoveBg
import java.io.File
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: FaceLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1


    init {
        initPaints()
    }

    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if(results == null || results!!.faceLandmarks().isEmpty()) {
            clear()
            return
        }
        val textPaint = Paint().apply {
            color = Color.RED
            textSize = 30f
            style = Paint.Style.FILL
        }

        results?.let { faceLandmarkerResult ->


//            for ((landmarkIndex, landmark) in faceLandmarkerResult.faceLandmarks().withIndex()) {
//                for ((pointIndex, normalizedLandmark) in landmark.withIndex()) {
//                    val x = normalizedLandmark.x() * imageWidth * scaleFactor
//                    val y = normalizedLandmark.y() * imageHeight * scaleFactor
//                    canvas.drawPoint(x, y, pointPaint)
//
//                    // Draw the index next to the point
//                    canvas.drawText(
//                        "$pointIndex",
//                        x + 10, // Slight offset to avoid overlap
//                        y + 10,
//                        textPaint
//                    )
//                }
//            }
            val globalImagePath = MainViewModel.GlobalImagePath.filePath
            if (globalImagePath != null) {
                val file = File(globalImagePath)
                if (file.exists()) {
                    val drawableImage = BitmapFactory.decodeFile(globalImagePath)
                    // Use the bitmap
                    if (drawableImage != null) {
                        var startX = 0f
                        var startY = 0f
                        var endX = 0f
                        var endY = 0f

                        // Define a Paint object for dots
                        val dotPaint = Paint().apply {
                            color = Color.RED // Set your desired color
                            style = Paint.Style.FILL
                            isAntiAlias = true
                            strokeWidth = 8f // Thickness of the dots
                        }

                        for ((landmarkIndex, landmark) in faceLandmarkerResult.faceLandmarks().withIndex()) {
                            var startX: Float = 0f
                            var startY: Float = 0f
                            var endX: Float = 0f
                            var endY: Float = 0f

                            for ((pointIndex, normalizedLandmark) in landmark.withIndex()) {
                                when (pointIndex) {
                                    355 -> {
                                        // Get the coordinates for point 360
                                        startX = normalizedLandmark.x() * imageWidth * scaleFactor
                                        startY = normalizedLandmark.y() * imageHeight * scaleFactor
                                    }

                                    360 -> {
                                        // Get the coordinates for point 355
                                        endX = normalizedLandmark.x() * imageWidth * scaleFactor
                                        endY = normalizedLandmark.y() * imageHeight * scaleFactor
                                    }
                                }
                            }

                            // Ensure the points are valid and we have the coordinates for both points
                            if (startX != 0f && startY != 0f && endX != 0f && endY != 0f) {
                                // Calculate the distance between points 355 and 360 (for scaling purposes)
                                val distanceX = Math.abs(endX - startX)
                                val distanceY = Math.abs(endY - startY)

                                // Set the max and min widths for the image (you can adjust these values)
                                val maxImageWidth = 300f   // Max width for the image
                                val minImageWidth = 100f   // Min width for the image

                                // Calculate the proportional width based on the distance (scaling factor)
                                val overlayWidth = Math.min(Math.max(distanceY, minImageWidth), maxImageWidth)

                                // Maintain the aspect ratio based on the width of the drawable image
                                val overlayHeight = drawableImage.height * (overlayWidth / drawableImage.width)

                                // Apply a scaling factor of 10 based on the difference in width and height
                                val scaleFactor = 2f
                                val scaledOverlayWidth = overlayWidth * scaleFactor
                                val scaledOverlayHeight = overlayHeight * scaleFactor

                                // Scale the bitmap to the desired size (keeping the aspect ratio)
                                val scaledBitmap = Bitmap.createScaledBitmap(
                                    drawableImage,
                                    scaledOverlayWidth.toInt(),
                                    scaledOverlayHeight.toInt(),
                                    true
                                )

                                // Calculate the center position between points 355 and 360
                                val centerX = (startX + endX) / 2
                                val centerY = (startY + endY) / 2

                                // Adjust the bitmap placement by offsetting it so it's centered at the midpoint
                                canvas.drawBitmap(
                                    scaledBitmap,
                                    centerX - (scaledOverlayWidth / 2),
                                    centerY - (scaledOverlayHeight / 2),
                                    null
                                )
                            } else {
                                Log.e("OverlayImage", "Invalid coordinates for points 355 or 360!")
                            }
                        }


                    }
                }
            }

                        //val drawableImage = BitmapFactory.decodeResource(context.resources, R.drawable.necklace)





            FaceLandmarker.FACE_LANDMARKS_CONNECTORS.forEach {
                canvas.drawLine(
                    faceLandmarkerResult.faceLandmarks().get(0).get(it!!.start()).x() * imageWidth * scaleFactor,
                    faceLandmarkerResult.faceLandmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor,
                    faceLandmarkerResult.faceLandmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor,
                    faceLandmarkerResult.faceLandmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor,
                    linePaint)
            }
        }
    }

    fun setResults(
        faceLandmarkerResults: FaceLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = faceLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
        private const val TAG = "Face Landmarker Overlay"
    }
}
