package kr.ac.hallym.prac01

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// number of coordinates per vertex in this array
// 이 배열에서 정점당 좌표 수
const val COORDS_PER_VERTEX = 3

class MyTriangle {

    private val triangleCoords = floatArrayOf( // in counterclockwise order: 반시계 방향으로
        0.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.0f,
        -0.5f, 0.5f, 0.0f,
    )

    private val color = floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f)
    /*private val triangleColors = floatArrayOf (
        1.0f, 1.0f, 1.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 1.0f,
        0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    )*/

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float) | 정점 갯수 * 4바이트
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order | 디바이스 하드웨어의 기본 바이트 순서 사용
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer | 바이트 버퍼로부터 floating pont buffer를 생성 
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer | 정점들을 FloatBuffer에 추가
                put(triangleCoords)
                // set the buffer to read the first coordinate | 버퍼가 첫번째 정점을 읽도록 설정
                position(0)
            }
        }

    /*private var colorBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleColors.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleColors)
                // set the buffer to read the first coordinate
                position(0)
            }
        }*/

    private val vertexShaderCode =
        "#version 300 es            \n" +   // OpenGL 버전
        "layout(location = 0) in vec4 vPosition; \n" +  // vec4 형식(4개의 요소를 가진 4차원 벡터)인 vPosition(vertex position)이 입력됨
        //"layout(location = 2) in vec4 vColor; \n" +
        //"out vec4 fColor; \n" +
        "void main() {              \n" +
        "   gl_PointSize = 5.0f;    \n" +   // 그려지는 점의 크기
        "   gl_Position = vPosition;\n" +   // 입력 변수 vPosition에 저장된 위치 정보가 gl_Position에 복사되어 해당 정점의 위치가 설정됨
        //"   fColor = vColor;        \n" +
        "}                          \n"

    private val fragmentShaderCode =
        "#version 300 es            \n" +
        "precision mediump float;   \n" +   // 부동소수점 연산의 정확도 설정
        "uniform vec4 vColor;       \n" +   // vec4 형식(4개의 요소를 가진 4차원 벡터)인 vColor가 입력됨 uniform으로 사용하여 프로그램 외부에서 쉐이더 내에서 사용하는 값을 동적으로 변경할 수 있게함
        //"in vec4 fColor;        \n" +
        "out vec4 fragColor;        \n" +   // 프래그먼트 쉐이더가 실행되고 나면 해당 색상 값이 출력됨
        "void main() {              \n" +
        "   fragColor = vColor;     \n" +   // 프래그먼트 쉐이더는 실행 중에 출력할 색상 값을 "fragColor"에 저장함
        "}                          \n"

    private var mProgram: Int = -1

    //private var mPositionHandle: Int = -1
    private var mColorHandle: Int = -1

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX // 좌표 수 / 정점당 좌표 수 = 정점 갯수
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex | 정점 1개당 가지는 좌표 값은 3개(x, y, z) * 4byte 즉, 정점배열의 총 크기

    init {
        val vertexShader: Int = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        /*  GLES30.GL_VERTEX_SHADER 상수는 정점 쉐이더(vertex shader) 유형을 의미함 정점 쉐이더는 3D 모델의 모양과 위치 등을 결정하는데 중요한 역할을 함
            "vertexShaderCode"는 loadShader 함수에서 쉐이더 객체로 컴파일되며, 컴파일된 쉐이더 객체의 ID가 "vertexShader" 변수에 저장됨
            loadShader 함수는 OpenGL ES API 중 하나인 glCreateShader를 호출하여 새로운 쉐이더 객체를 생성하고, glShaderSource 함수를 호출하여 쉐이더 소스 코드를 적용함 
            그리고 glCompileShader 함수를 호출하여 쉐이더를 컴파일함 만약 컴파일이 성공하면, 쉐이더 객체의 ID를 반환함
            프래그먼트 쉐이더 코드도 동일한 방식으로 컴파일하고 쉐이더 객체의 ID를 가져와서 렌더링 파이프라인을 완성하게 됨     */

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
            GLES30.glEnableVertexAttribArray(0)

            // Prepare the triangle coordinate data
            GLES30.glVertexAttribPointer(
                0,
                COORDS_PER_VERTEX,
                GLES30.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )
        //}

        //get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor").also {

            // Set color for drawing the triangle
            GLES30.glUniform4fv(it, 1, color, 0)
        }

        /*GLES30.glEnableVertexAttribArray(2)
        GLES30.glVertexAttribPointer(
            2,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            colorBuffer
        )*/
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
        GLES30.glLineWidth(5.0f)

        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}