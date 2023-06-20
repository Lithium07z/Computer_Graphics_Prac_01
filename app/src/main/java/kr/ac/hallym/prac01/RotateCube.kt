package kr.ac.hallym.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac01.databinding.ActivityRotateCubeBinding

var rotateAxis = 0; // 0: x-axis, 1: y-axis, 2: z-axis
var scaleFactor = 1f
var displace = floatArrayOf(0f, 0f, 0f)

class RotateCube : AppCompatActivity() {
    val binding: ActivityRotateCubeBinding by lazy {
        ActivityRotateCubeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_rotate_cube)
        initGLSurfaceView()
        setContentView(binding.root)

        binding.rotateX.setOnClickListener {
            rotateAxis = 0;
        }

        binding.rotateY.setOnClickListener {
            rotateAxis = 1;
        }

        binding.rotateZ.setOnClickListener {
            rotateAxis = 2;
        }

        binding.scaleUp.setOnClickListener {
            scaleFactor *= 1.1f
        }

        binding.scaleDown.setOnClickListener {
            scaleFactor *= 0.9f
        }

        binding.posX.setOnClickListener {
            displace[0] += 0.1f
        }

        binding.negX.setOnClickListener {
            displace[0] -= 0.1f
        }

        binding.posY.setOnClickListener {
            displace[1] += 0.1f
        }

        binding.negY.setOnClickListener {
            displace[1] -= 0.1f
        }

        binding.posZ.setOnClickListener {
            displace[2] += 0.1f
        }

        binding.negZ.setOnClickListener {
            displace[2] -= 0.1f
        }

    }

    fun initGLSurfaceView() {
        //Create an OpenGL ES 3.0 context
        binding.surfaceView.setEGLContextClientVersion(3)
        // Set the Renderer for drawing on the GLSurfaceView
        binding.surfaceView.setRenderer(MainGLRenderer(this))
        // Render the view only when there is a change in the drawing data
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}