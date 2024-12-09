package com.example.faceland

import android.os.Bundle
import android.view.ScaleGestureDetector
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.faceland.databinding.ActivityMainBinding
import com.google.mediapipe.examples.facelandmarker.MainViewModel
import com.google.mediapipe.examples.facelandmarker.OverlayView
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private var scaleFactor = 1f  // Current scale factor (initially 1)
    private var scaleDetector: ScaleGestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        activityMainBinding.navigation.setupWithNavController(navController)
        activityMainBinding.navigation.setOnNavigationItemReselectedListener {
            // ignore the reselection
        }
        val job = CoroutineScope(Dispatchers.Main).launch {
            viewModel.removeBackgroundAndSave(application, "https://cdn.caratlane.com/media/catalog/product/cache/6/image/480x480/9df78eab33525d08d6e5fb8d27136e95/J/N/JN00267-1YP900_11_listfront.jpg")
        }
        scaleDetector = ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor // Adjust scale factor based on pinch gesture
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)) // Set min and max scale limits
               // invalidate() // Redraw the view after scaling the image
                return true
            }
        })
        viewModel.outputImage.observe(this, Observer { bitmap ->
            // Check if bitmap is not null and set it to the ImageView
            bitmap?.let {

            }
        })

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}