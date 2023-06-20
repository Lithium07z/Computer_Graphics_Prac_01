package kr.ac.hallym.prac01

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin
import kotlin.math.cos

class MainGLRenderer(val context: Context): GLSurfaceView.Renderer {

    private lateinit var mTriangle: MyTriangle
    private lateinit var mSquare: MySquare
    private lateinit var mCube: MyColorCube
    private lateinit var mHexaPyramid: MyHexaPyramid
    private lateinit var mGround: MyGround
    private lateinit var mTexCube: MyTexCube
    private lateinit var mTexPillar: MyTexPillar
    private lateinit var mTexGround: MyTexGround

    // MVPMatrix is an abbreviation for "Model View Projection Matrix"
    private var mvpMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private var projectionMatrix = FloatArray(16)
    private var vpMatrix = FloatArray(16)
    private var modelMatrix = floatArrayOf (
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    )

    private var startTime = SystemClock.uptimeMillis()
    private var rotAngles = floatArrayOf(0f, 0f, 0f)
    private var aspectRatio = 1.0f

//    private val startTime by lazy {
//        SystemClock.uptimeMillis()
//    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background frame color
        GLES30.glClearColor(0.2f, 0.2f, 0.2f, 1.0f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        GLES30.glEnable(GLES30.GL_POLYGON_OFFSET_FILL)
        GLES30.glPolygonOffset(1f, 1f)

        Matrix.setIdentityM(mvpMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(vpMatrix, 0)

        when (drawMode) {
            1 -> mTriangle = MyTriangle() // initialize a triangle
            2 -> mSquare = MySquare() // initialize a square
            3, 5 -> mCube = MyColorCube()
            4 -> mHexaPyramid = MyHexaPyramid()
            6 -> {
                mCube = MyColorCube()
                mHexaPyramid = MyHexaPyramid()
            }
            7 -> {
                mCube = MyColorCube()
                mGround = MyGround()
            }
            8 -> {
                mTexCube = MyTexCube(context)
                mTexPillar = MyTexPillar(context)
                mTexGround = MyTexGround(context)
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        aspectRatio = width.toFloat() / height.toFloat()

        when (drawMode) {
            3, 4, 7, 8 -> {
                val ratio: Float = width.toFloat() / height.toFloat()
                Matrix.perspectiveM(projectionMatrix, 0, 90f, ratio, 0.001f, 1000f)
            }
            5, 6 -> {
                if (width > height) {
                    val ratio: Float = width.toFloat() / height.toFloat()
                    Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 0f, 1000f)
                } else {
                    val ratio: Float = height.toFloat() / width.toFloat()
                    Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, 0f, 1000f)
                }
            }
        }

        when(drawMode) {
            3, 5 -> {
                Matrix.setLookAtM(viewMatrix, 0, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
            }
            4, 7 -> {
                Matrix.setLookAtM(viewMatrix, 0, 2f, 2f , 2f , 0f, 0f, 0f, 0f, 1f, 0f)
            }
            6 -> {
                Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 2f, 0f, 0f, 0f, 0f, 1f, 0f)
            }
            8 -> {
                Matrix.setLookAtM(viewMatrix, 0, 0f, 3f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)
            }
        }

        // Calculate the projection maxtrix and view transformation
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Redraw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        when (drawMode) {
            5 -> {
                Matrix.setIdentityM(modelMatrix, 0)

                // Create a rotation transformation for the triangle
                val endTime = SystemClock.uptimeMillis()    // 현재 시간
                val angle = 0.001f * (endTime - startTime).toFloat()    // (현재 시간 - 시작 시간) 만큼 회전
                startTime = endTime     // 시작 시간을 현재 시간으로 바꿈
                rotAngles[rotateAxis] += angle      // rotateAxis(x or y or z)에 angle 값 누적합
                var sinAngle = sin(rotAngles[0])    // 0 == rotateAxis == x축 이므로 x축 회전각
                var cosAngle = cos(rotAngles[0])
                val rotXMatrix = floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, cosAngle, sinAngle, 0f,
                    0f, -sinAngle, cosAngle, 0f,
                    0f, 0f, 0f, 1f
                ) // 전치 행렬
                sinAngle = sin(rotAngles[1])
                cosAngle = cos(rotAngles[1])
                val rotYMatrix = floatArrayOf(
                    cosAngle, 0f, -sinAngle, 0f,
                    0f, 1f, 0f, 0f,
                    sinAngle, 0f, cosAngle, 0f,
                    0f, 0f, 0f, 1f,
                ) // 전치 행렬
                sinAngle = sin(rotAngles[2])
                cosAngle = cos(rotAngles[2])
                val rotZMatrix = floatArrayOf(
                    cosAngle, sinAngle, 0f, 0f,
                    -sinAngle, cosAngle, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f,
                ) // 전치 행렬
                Matrix.multiplyMM(modelMatrix, 0, rotYMatrix, 0 ,rotXMatrix, 0) // modelMatrix = YXMatrix
                Matrix.multiplyMM(modelMatrix, 0, rotZMatrix, 0, modelMatrix, 0) // modelMatrix = ZYXMatrix

                val scaleMatrix = floatArrayOf (
                    scaleFactor, 0f, 0f, 0f,
                    0f, scaleFactor, 0f, 0f,
                    0f, 0f, scaleFactor, 0f,
                    0f, 0f, 0f, 1f,
                )
                Matrix.multiplyMM(modelMatrix, 0,  modelMatrix, 0, scaleMatrix, 0) // model Matrix = ZYXSMatrix

                val translateMatrix = floatArrayOf (
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    displace[0], displace[1], displace[2], 1f   // 사람은 행으로 읽어서 주지만 디바이스는 열로 읽음 따라서 우리가 생각한 방향과 반대이므로 배열을 전치함
                )
                Matrix.multiplyMM(modelMatrix, 0, translateMatrix, 0, modelMatrix, 0) // model Matrix = TZYXSMatrix
            }
            6 -> {
                Matrix.setIdentityM(modelMatrix, 0)

                // Create a rotation transformation for the triangle
                val endTime = SystemClock.uptimeMillis()
                val angle = 0.1f * (endTime - startTime).toInt()
                startTime = endTime
                if (isRotating) {
                    rotAngles[rotateAxis] += angle
                }

                Matrix.setRotateM(modelMatrix, 0, rotAngles[0], 1f, 0f, 0f)
                val tempMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
                Matrix.setRotateM(tempMatrix, 0, rotAngles[1], 0f, 1f, 0f)
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)
                Matrix.setRotateM(tempMatrix, 0, rotAngles[2], 0f, 0f, 1f)
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)

                Matrix.scaleM(tempMatrix, 0, 0.5f, 0.5f, 0.5f)
                Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, tempMatrix, 0)

//                Matrix.setIdentityM(tempMatrix, 0)
//                Matrix.translateM(tempMatrix, 0, 0f, -0.5f, 0f)
//                Matrix.multiplyMM(modelMatrix, 0 , tempMatrix, 0, modelMatrix, 0)
            }
        }

        // Combine the rotation matrix with the projection and camera view
        // Note that the viewMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0) // mvpMatrix = pvTZYXSMatrix

        when (drawMode) {
            1 -> mTriangle.draw()
            2 -> mSquare.draw()
            3 -> mCube.draw(vpMatrix)
            4 -> mHexaPyramid.draw(vpMatrix)
            5 -> mCube.draw(mvpMatrix)
            6 -> {
                val rotMatrix = modelMatrix.copyOf(16)
                val tempMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
                Matrix.translateM(tempMatrix, 0, -0.5f, -0.5f, 0f)
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)

                Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

                mCube.draw(mvpMatrix)

                Matrix.transposeM(modelMatrix, 0, rotMatrix, 0)
                Matrix.setIdentityM(tempMatrix, 0)
                Matrix.translateM(tempMatrix, 0, 0.5f, -0.5f, 0f)
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)

                Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

                mCube.draw(mvpMatrix)

                modelMatrix = rotMatrix.copyOf(16);
                Matrix.setIdentityM(tempMatrix, 0)
                Matrix.translateM(tempMatrix, 0, 0.5f, 0.5f, 0f)
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)

                Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

                mHexaPyramid.draw(mvpMatrix)

                Matrix.transposeM(modelMatrix, 0, rotMatrix, 0)
                Matrix.setIdentityM(tempMatrix, 0)
                Matrix.translateM(tempMatrix, 0, -0.5f, 0.5f, 0f)
                Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, modelMatrix, 0)

                Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

                mHexaPyramid.draw(mvpMatrix)
            }
            7 -> {
                if (viewMode == 0) {
                    Matrix.perspectiveM(projectionMatrix, 0, 90f, aspectRatio, 0.001f, 1000f)
                }
                else {
                    if (aspectRatio >= 1.0f) {
                        Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 0f, 1000f)
                    } else {
                        val ratio = 1.0f / aspectRatio
                        Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, 0f, 1000f)
                    }
                }

                eyeAt[0] = eyePos[0] + cameraVec[0]
                eyeAt[1] = eyePos[1] + cameraVec[1]
                eyeAt[2] = eyePos[2] + cameraVec[2]
                Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0], eyeAt[1], eyeAt[2], 0f, 1f, 0f)

                Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

                mCube.draw(vpMatrix)
                mGround.draw(vpMatrix)
            }
            8-> {
                eyeAt[0] = eyePos[0] + cameraVec[0]
                eyeAt[1] = eyePos[1] + cameraVec[1]
                eyeAt[2] = eyePos[2] + cameraVec[2]
                Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0], eyeAt[1], eyeAt[2], 0f, 1f, 0f)

                Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

                for (z in -5 .. 0 step 2) {
                    Matrix.setIdentityM(modelMatrix, 0)
                    Matrix.translateM(modelMatrix, 0, 3f, 0f, z.toFloat())
                    Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
                    mTexPillar.draw(mvpMatrix)

                    Matrix.setIdentityM(modelMatrix, 0)
                    Matrix.translateM(modelMatrix, 0, -3f, 0f, z.toFloat())
                    Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
                    mTexPillar.draw(mvpMatrix)
                }
                mTexCube.draw(vpMatrix)
                mTexGround.draw(vpMatrix)
            }
        }
    }
}