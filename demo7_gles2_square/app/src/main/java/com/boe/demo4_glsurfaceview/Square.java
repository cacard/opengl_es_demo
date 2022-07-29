package com.boe.demo4_glsurfaceview;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    // 顶点Shader
    private static final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "void main() {\n" +
                    "  gl_Position = vPosition;\n" +
                    "}\n";

    // Fragment Shader
    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = vColor;\n" +
                    "}\n";

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    static final int COORDS_PER_VERTEX = 3;

    // 顶点：不重复
    static float squareCoords[] = {
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f}; // top right

    // 顶点索引
    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    float[] color = {1, 0.1f, 0.6f, 1f};
    int program;

    public Square() {
        // VBO
        // ------------
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // EBO?
        //-------------
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // 编译Shader/Program
        program = ShaderHelper.linkProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void draw() {
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
                3 * 4, //stride:步长
                vertexBuffer //数据
        );

        // 更新uniform
        // --------------
        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 绘制
        // 这里使用了drawElements，就是按EBO绘制
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,  //EBO的长度
                GLES20.GL_UNSIGNED_SHORT, //!EBO的类型
                drawListBuffer //EBO
        );

        // 释放
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
