package kr.ac.hallym.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac01.databinding.ActivityDrawTexObjectsBinding
import kotlin.math.sin

class DrawTexObjects : AppCompatActivity() {
    val binding: ActivityDrawTexObjectsBinding by lazy {
        ActivityDrawTexObjectsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_draw_tex_objects)
        supportActionBar?.hide()
        initSurfaceView()
        setContentView(binding.root)

        eyePos = floatArrayOf(0f, 1f, 2f)
        cameraVec = floatArrayOf(0f, -0.44721f, -0.89443f)

        val sinTheta = 0.17365f
        val cosTheta = 0.98481f
        binding.eyeLeft.setOnClickListener {
            var newVecz = cosTheta * cameraVec[2] - sinTheta * cameraVec[0]
            var newVecX = sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
            cameraVec[0] = newVecX
            cameraVec[2] = newVecz
            binding.surfaceView.requestRender()
        }
        binding.eyeRight.setOnClickListener {
            var newVecz = cosTheta * cameraVec[2] + sinTheta * cameraVec[0]
            var newVecX = -sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
            cameraVec[0] = newVecX
            cameraVec[2] = newVecz
            binding.surfaceView.requestRender()
        }
        binding.eyeForward.setOnClickListener {
            var newPosX = eyePos[0] + 0.5f * cameraVec[0]
            var newPosZ = eyePos[2] + 0.5f * cameraVec[2]
            if (newPosX > -10 && newPosX < 10 && newPosZ > -10 && newPosZ < 10) {
                eyePos[0] = newPosX
                eyePos[2] = newPosZ
                binding.surfaceView.requestRender()
            }
        }
        binding.eyeBackward.setOnClickListener {
            var newPosX = eyePos[0] - 0.5f * cameraVec[0]
            var newPosZ = eyePos[2] - 0.5f * cameraVec[2]
            if (newPosX > -10 && newPosX < 10 && newPosZ > -10 && newPosZ < 10) {
                eyePos[0] = newPosX
                eyePos[2] = newPosZ
                binding.surfaceView.requestRender()
            }
        }
    }

    fun initSurfaceView() {
        binding.surfaceView.setEGLContextClientVersion(3)
        binding.surfaceView.setRenderer(MainGLRenderer(this))
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}