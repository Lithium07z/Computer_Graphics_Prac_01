package kr.ac.hallym.prac01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DrawColorCube : AppCompatActivity() {

    private lateinit var mainSurfavceView: MainGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mainSurfavceView = MainGLSurfaceView(this)
        setContentView(mainSurfavceView)
    }
}