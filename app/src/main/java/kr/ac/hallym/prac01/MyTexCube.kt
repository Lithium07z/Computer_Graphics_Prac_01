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

class MyTexCube(val myContext: Context) {
    private val drawOrder = intArrayOf(
        0, 3, 2, 0, 2, 1,
        2, 3, 7, 2, 7, 6,
        1, 2, 6, 1, 6, 5,
        4, 0, 1, 4, 1, 5,
        3, 0 ,4, 3, 4, 7,
        5, 6, 7, 5, 7, 4,
    )

    private val vertexCoords = FloatArray(108).apply {
        val vertex = arrayOf (
            floatArrayOf(-0.5f, 0.5f, -0.5f),
            floatArrayOf(-0.5f, -0.5f, -0.5f),
            floatArrayOf(0.5f, -0.5f, -0.5f),
            floatArrayOf(0.5f, 0.5f, -0.5f),
            floatArrayOf(-0.5f, 0.5f, 0.5f),
            floatArrayOf(-0.5f, -0.5f, 0.5f),
            floatArrayOf(0.5f, -0.5f, 0.5f),
            floatArrayOf(0.5f, 0.5f, 0.5f)
        )
        var index = 0
        for (i in 0 .. 35) {
            this[index++] = vertex[drawOrder[i]][0]
            this[index++] = vertex[drawOrder[i]][1]
            this[index++] = vertex[drawOrder[i]][2]
        }
    }

    private val vertexUVs = FloatArray(72).apply {
        val UVs = arrayOf (
            floatArrayOf(0.0f, 0.0f),
            floatArrayOf(0.0f, 1.0f),
            floatArrayOf(1.0f, 1.0f),
            floatArrayOf(0.0f, 0.0f),
            floatArrayOf(1.0f, 1.0f),
            floatArrayOf(1.0f, 0.0f)
        )
        var index = 0
        for (i in 0 .. 5) {
            for (j in 0 .. 5) {
                this[index++] = UVs[j][0]
                this[index++] = UVs[j][1]
            }
        }
    }

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
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
        "layout(location = 8) in vec4 vPosition;       \n" +
        "layout(location = 9) in vec2 vTexCoord;       \n" +
        "out vec2 fTexCoord;           \n" +
        "void main() {                 \n" +
        "   gl_Position = uMVPMatrix * vPosition;       \n" +
        "   fTexCoord = vTexCoord;     \n" +
        "}                             \n"

    private val fragmentShaderCode =
        "#version 300 es            \n" +
        "precision mediump float;   \n" +
        "uniform sampler2D sTexture;\n" +
        "in vec2 fTexCoord;         \n" +
        "out vec4 fragColor;        \n" +
        "void main() {              \n" +
        "   fragColor = texture(sTexture, fTexCoord);     \n" +
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

        GLES30.glEnableVertexAttribArray(8)
        GLES30.glVertexAttribPointer(
            8,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(9)
        GLES30.glVertexAttribPointer(
            9,
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
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0 , loadBitmap("crate.bmp"), 0)
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