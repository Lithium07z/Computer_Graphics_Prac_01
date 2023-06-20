package kr.ac.hallym.prac01

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MySquare {

    private val squareCoords = floatArrayOf( // in counterclockwise order:
        -0.75f, 0.75f, 0.0f,
        -0.75f, -0.75f, 0.0f,
        0.75f, -0.75f, 0.0f,
        0.75f, 0.75f, 0.0f,
    )

    private val color = floatArrayOf(0.0f, 0.0f, 0.5f, 1.0f)
    private val squareColors = floatArrayOf (
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 1.0f,
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices | 인덱스 배열

    private var vertexBuffer: FloatBuffer =
        // (numbre of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(squareCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var colorBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareColors.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(squareColors)
                // set the buffer to read the first coordinate
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
        "layout(location = 1) in vec4 vPosition;    \n" +   // 정점 좌표를 입력으로 받음
        "layout(location = 2) in vec4 vColor;       \n" +   // 정점 색상을 입력으로 받음
        "out vec4 fColor;           \n" +   // 프래그먼트 셰이더로 전달할 색상 값을 출력으로 설정
        "void main() {              \n" +
        "   gl_Position = vPosition;\n" +   // 정점 좌표 값을 gl_Position에 대입
        "   fColor = vColor;        \n" +   // 정점 색상 값을 fColor에 대입
        "}                          \n"

    private val fragmentShaderCode =
        "#version 300 es            \n" +
        "precision mediump float;   \n" +
        "in vec4 fColor;            \n" +   // 정점 색상 값을 입력으로 받음
        "out vec4 fragColor;        \n" +   // 출력으로 전달할 색상 값을 설정
        "void main() {              \n" +
        "   fragColor = fColor;     \n" +   // fColor 값을 fragColor에 대입
        "}                          \n"

    private var mProgram: Int = -1

    //private var mPositionHandle: Int = -1
    //private var mColorHandle: Int = -1

    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

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

        // get handle to vertex shader's vPosition member
        //mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES30.glEnableVertexAttribArray(1)

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(
                1,  // GPU 1번 메모리에서 실행
                COORDS_PER_VERTEX,  // 정점 하나를 표현하는데 사용되는 좌표 수, 3차원이므로 3
                GLES30.GL_FLOAT,    // float형으로 사용됨
                false,  // 정규화 안함
                vertexStride,   // 모든 정점을 표현하는데 사용된 좌표 갯수
                vertexBuffer
            )
        //}

        //get handle to fragment shader's vColor member
        /*mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also {

            // Set color for drawing the triangle
            GLES30.glUniform4fv(it, 1, color, 0)
        }*/

        GLES30.glEnableVertexAttribArray(2)
        GLES30.glVertexAttribPointer(
            2,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )
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

    fun draw() {

        GLES30.glUseProgram(mProgram)

        // Draw the square
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}