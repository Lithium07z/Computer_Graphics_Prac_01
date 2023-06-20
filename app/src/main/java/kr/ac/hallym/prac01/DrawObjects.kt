package kr.ac.hallym.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac01.databinding.ActivityDrawObjectsBinding

var isRotating: Boolean = false
class DrawObjects : AppCompatActivity() {
    val binding: ActivityDrawObjectsBinding by lazy {
        ActivityDrawObjectsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        initGLSurfaceView()
        setContentView(binding.root)

        binding.rotateX.setOnClickListener {
            rotateAxis = 0
        }
        binding.rotateY.setOnClickListener {
            rotateAxis = 1
        }
        binding.rotateZ.setOnClickListener {
            rotateAxis = 2
        }
        binding.toggleBtn.setOnClickListener {
            isRotating = !isRotating
            if (isRotating) {
                binding.toggleBtn.text = "Stop"
            } else {
                binding.toggleBtn.text = "Start"
            }
        }
    }

    fun initGLSurfaceView() {
        // Create an OpenGL ES 3.0 context
        binding.surfaceView.setEGLContextClientVersion(3)
        // Set the Renderer for drawing on the GLSurfaceView
        binding.surfaceView.setRenderer(MainGLRenderer(this))
        // Render the view only when there is a change in the drawing data
        // binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}