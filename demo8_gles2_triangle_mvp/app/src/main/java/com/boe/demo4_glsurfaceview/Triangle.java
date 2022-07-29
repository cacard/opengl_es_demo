package com.boe.demo4_glsurfaceview;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    // 顶点Shader
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 vPosition;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * vPosition;\n" +
                    "}\n";

    // Fragment Shader
    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = vColor;\n" +
                    "}\n";

    private final FloatBuffer vertexBuffer;

    // 每个顶点的有3个float数据
    static final int COORDS_PER_VERTEX = 3;

    static final int SIZE_OF_FLOAT = 4;

    // 逆时针定义坐标点
    static float[] triangleCoords = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float[] color = {1, 0.1f, 0.6f, 1f};

    int program;

    public Triangle() {

        // VBO
        // ----------------------
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // 编译Shader/Program
        program = ShaderHelper.linkProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);

        // 顶点属性：位置
        // --------------
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, //index:
                3, //size:
                GLES20.GL_FLOAT,
                false,
                3 * SIZE_OF_FLOAT, //stride:步长
                vertexBuffer //数据
        );

        //更新uniform
        int mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0);

        // 更新uniform
        // --------------
        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // 释放
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
