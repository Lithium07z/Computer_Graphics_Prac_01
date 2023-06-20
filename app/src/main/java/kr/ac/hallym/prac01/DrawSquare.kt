package kr.ac.hallym.prac01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DrawSquare : AppCompatActivity() {

    private lateinit var mainSurfaceView: MainGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mainSurfaceView = MainGLSurfaceView(this)
        setContentView(mainSurfaceView)
    }
}