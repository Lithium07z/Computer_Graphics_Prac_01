package kr.ac.hallym.prac01

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac01.databinding.ActivityViewObjectsBinding

var viewMode = 0 // 0: perspective, 1: orthographic viewing
var eyePos = floatArrayOf(2.0f, 2.0f, 2.0f)
var eyeAt = floatArrayOf(0.0f, 0.0f, 0.0f)
var cameraVec = floatArrayOf(-0.57735f, -0.57735f, -0.57735f)

class ViewObjects : AppCompatActivity() {
    val binding: ActivityViewObjectsBinding by lazy {
        ActivityViewObjectsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        initSurfaceView()
        setContentView(binding.root)

        binding.ortho.setOnClickListener {
            viewMode = 1
            binding.surfaceView.requestRender()
        }
        binding.persp.setOnClickListener {
            viewMode = 0
            binding.surfaceView.requestRender()
        }

        val sinTheta = 0.17365f
        val cosTheta = 0.98481f
        binding.eyeLeft.setOnClickListener {
            var newVecZ = cosTheta * cameraVec[2] - sinTheta * cameraVec[0]
            var newVecX = sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
            cameraVec[0] = newVecX
            cameraVec[2] = newVecZ
            binding.surfaceView.requestRender()
        }
        binding.eyeRight.setOnClickListener {
            var newVecZ = cosTheta * cameraVec[2] + sinTheta * cameraVec[0]
            var newVecX = -sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
            cameraVec[0] = newVecX
            cameraVec[2] = newVecZ
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