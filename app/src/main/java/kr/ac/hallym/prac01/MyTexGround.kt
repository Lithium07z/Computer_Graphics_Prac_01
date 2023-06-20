package kr.ac.hallym.prac01

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyTexGround(val myContext: Context) {
    private val vertexCoords = floatArrayOf( // in counterclockwise order:
        -10.0f, -1.0f, -10.0f,
        -10.0f, -1.0f, 10.0f,
        10.0f, -1.0f, 10.0f,
        -10.0f, -1.0f, -10.0f,
        10.0f, -1.0f, 10.0f,
        10.0f, -1.0f, -10.0f,
    )

    private val vertexUVs = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 20.0f,
        20.0f, 20.0f,
        0.0f, 0.0f,
        20.0f, 20.0f,
        20.0f, 0.0f,
    )

    private var vertexBuffer: FloatBuffer =
        // (numbre of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(vertexCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var uvBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexUVs.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexUVs)
                position(0)
            }
        }

    private val vertexShaderCode =
        "#version 300 es            \n" +
        "uniform mat4 uMVPMatrix;    \n" +
        "layout(location = 10) in vec4 vPosition;       \n" +   // 정점 좌표를 입력으로 받음
        "layout(location = 11) in vec2 vTexCoord;       \n" +   // 정점 색상을 입력으로 받음
        "out vec2 fTexCoord;           \n" +   // 프래그먼트 셰이더로 전달할 색상 값을 출력으로 설정
        "void main() {                 \n" +
        "   gl_Position = uMVPMatrix * vPosition;       \n" +   // 정점 좌표 값을 gl_Position에 대입
        "   fTexCoord = vTexCoord;     \n" +   // 정점 색상 값을 fColor에 대입
        "}                             \n"

    private val fragmentShaderCode =
        "#version 300 es            \n" +
        "precision mediump float;   \n" +
        "uniform sampler2D sTexture;\n" +
        "in vec2 fTexCoord;         \n" +   // 정점 색상 값을 입력으로 받음
        "out vec4 fragColor;        \n" +   // 출력으로 전달할 색상 값을 설정
        "void main() {              \n" +
        "   fragColor = texture(sTexture, fTexCoord);     \n" +   // fColor 값을 fragColor에 대입
        "}                          \n"

    private var mProgram: Int = -1

    private var mvpMatrixHandle: Int = -1
    private var textureID = IntArray(1)

    private val vertexCount: Int = vertexCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(10)
        GLES30.glVertexAttribPointer(
            10,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(11)
        GLES30.glVertexAttribPointer(
            11,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")

        GLES30.glGenTextures(1, textureID, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        //GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE) // S축 방향으로 1보다 큰 위치는 모두 흰색 처리
        //GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE) // T축 방향으로 1보다 큰 위치는 모두 흰색 처리
        //GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_MIRRORED_REPEAT) // 좌우 대칭
        //GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_MIRRORED_REPEAT) // 상하 대칭
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0 , loadBitmap("logo.bmp"), 0)
    }

    private fun loadBitmap(filename: String): Bitmap {
        val manager = myContext.assets
        val inputStream = BufferedInputStream(manager.open(filename))
        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
        return bitmap!!
    }

    private fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        return GLES30.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            // log the compile error
            val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
            if (compiled.get(0) == 0) {
                GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
                if (compiled.get(0) > 1) {
                    Log.e("Shader", "$type shader: " + GLES30.glGetShaderInfoLog(shader))
                }
                GLES30.glDeleteShader(shader)
                Log.e("Shader", "$type shader compile error.")
            }
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glUseProgram(mProgram)
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}