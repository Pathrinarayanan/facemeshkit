/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
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
package com.google.mediapipe.examples.handlandmarker

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
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: HandLandmarkerResult? = null
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
        results?.let { handLandmarkerResult ->
            for ((index, landmark) in handLandmarkerResult.landmarks().withIndex()) {
                var pointX = 0f
                var pointY = 0f
                var pointX1 = 0f
                var pointY1 = 0f

                // Loop through landmarks to get the 13th and 14th points
                for ((pointIndex, normalizedLandmark) in landmark.withIndex()) {
                    if (pointIndex == 13) {
                        // Calculate the position for the 13th point (usually wrist or base of the hand)
                        pointX = normalizedLandmark.x() * imageWidth * scaleFactor
                        pointY = normalizedLandmark.y() * imageHeight * scaleFactor
                    }

                    if (pointIndex == 14) { // Focus on the 14th point (usually near the thumb or the other part of the hand)
                        pointX1 = normalizedLandmark.x() * imageWidth * scaleFactor
                        pointY1 = normalizedLandmark.y() * imageHeight * scaleFactor
                    }
                }

                // Calculate the distance between the two points (13 and 14)
                val distance = Math.sqrt(
                  Math.pow((pointY1 - pointY).toDouble(), 2.0)
                ).toFloat()

                // Load the image from resources
                val imageBitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.ring_without_bg)

                // Check if the imageBitmap is loaded correctly
                if (imageBitmap != null) {
                    // Define a base scale factor for the image
                    val baseScaleFactor =
                        1f // You can tweak this value for your desired image size

                    // Scale the image based on the distance
                    val scaledWidth = (imageBitmap.width * (distance /  1200f)).toInt()
                    val scaledHeight = (imageBitmap.height * (distance / 1200f)).toInt()

                    // Create the scaled bitmap
                    val scaledBitmap =
                        Bitmap.createScaledBitmap(imageBitmap, scaledWidth, scaledHeight, true)

                    // Center the image on the 13th point (pointX, pointY)
                    val imageLeft = pointX - (scaledWidth / 2f)  // Horizontal center
                    val imageTop = pointY1 - (scaledHeight / 2f) // Vertical center

                    // Draw the scaled image centered at the point
                    canvas.drawBitmap(scaledBitmap, imageLeft, imageTop+80f, null)
                } else {
                    Log.e("ImageError", "Failed to load image from resources.")
                }

            }




//                HandLandmarker.HAND_CONNECTIONS.forEach {
//                    canvas.drawLine(
//                        landmark.get(it!!.start())
//                            .x() * imageWidth * scaleFactor,
//                        landmark.get(it.start())
//                            .y() * imageHeight * scaleFactor,
//                        landmark.get(it.end())
//                            .x() * imageWidth * scaleFactor,
//                        landmark.get(it.end())
//                            .y() * imageHeight * scaleFactor,
//                        linePaint
//                    )
//                }
                }
            }




    fun setResults(
        handLandmarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handLandmarkerResults

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
        private const val LANDMARK_STROKE_WIDTH = 50F
    }
}