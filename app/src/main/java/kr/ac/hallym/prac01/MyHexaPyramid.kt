package kr.ac.hallym.prac01

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyHexaPyramid {

    private val vertexCoords = floatArrayOf( // in counterclockwise order:
        0.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.5f, 1.0f, -0.866f,
        -0.5f, 1.0f, -0.866f,
        -1.0f, 1.0f, 0.0f,
        -0.5f, 1.0f, 0.866f,
        0.5f, 1.0f, 0.866f,
        0.0f, -1.0f, 0.0f,
    )

    private val vertexColors = floatArrayOf(
        1.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 0.0f
    )

    private val drawOrder = shortArrayOf(
        0, 1, 2, // top
        0, 2, 3, // left-up
        0, 3, 4, // left-down
        0, 4, 5, // bottom
        0, 5, 6, // right-down
        0, 6, 1, // right-up
        7, 1, 6, // back
        7, 6, 5, // left-back
        7, 5, 4, // left_front
        7, 4, 3, // front
        7, 3, 2, // right-front
        7, 2, 1, // right-back
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

    private var colorBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(vertexColors.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexColors)
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        // (number of index values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexShaderCode =
        "#version 300 es            \n" +
        "uniform mat4 uMVPMatrix;   \n" +
        "layout(location = 5) in vec4 vPosition;    \n" +
        "layout(location = 6) in vec4 vColor;       \n" +
        "out vec4 fColor;           \n" +
        "void main() {              \n" +
        "   gl_Position = uMVPMatrix * vPosition;   \n" +
        "   fColor = vColor;        \n" +
        "}                          \n"

    private val fragmentShaderCode =
        "#version 300 es            \n" +
        "precision mediump float;   \n" +
        "in vec4 fColor;            \n" +
        "out vec4 fragColor;        \n" +
        "void main() {              \n" +
        "   fragColor = fColor;     \n" +
        "}                          \n"

    private var mProgram: Int = -1

    // Use to access and set the transformation matrix
    private var mvpMatrixHandle: Int = - 1

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also {

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram)

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(5)
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            5,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(6)
        GLES30.glVertexAttribPointer(
            6,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )

        // get handle to shape's transformation matrix
        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
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

        // Pass the projection and view transformation matrix to the shader
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}