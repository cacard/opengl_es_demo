package com.boe.gles_demo.shape;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.helper.CameraHelper;
import com.boe.gles_demo.Constants;
import com.boe.gles_demo.helper.LogHelper;
import com.boe.gles_demo.R;
import com.boe.gles_demo.helper.ShaderHelper;
import com.boe.gles_demo.helper.TextResReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 分割挡板线
 */
public class Divider {

    float[] vertexes = {
            // ---X,Y,Z--
            -0.5f, 0f, 0.05f,
            0.5f, 0f, 0.05f
    };

    // 分割线颜色
    float[] color = {1f, 0f, 0f, 1f};

    Context context;

    // 缓冲
    private FloatBuffer vertexBuffer;

    // program id
    int programId;

    // 顶点属性：位置属性
    int attribLocationPosition;

    public Divider(Context context) {
        this.context = context;

        // 准备缓冲数据
        // --------------
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertexes.length * Constants.BYTES_PER_FLOAT);
        buffer = buffer.order(ByteOrder.nativeOrder());
        vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(vertexes);
        vertexBuffer.position(0);

        // 编译连接shader
        // ---------------
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.divider_vertex_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.divider_fragment_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
        LogHelper.log("【Table】programId:" + programId);
    }

    public void draw(float angle, int width, int height) {
        // 切换program
        GLES20.glUseProgram(programId);

        // 重置缓冲区指针（每次绘制都要重新设置）
        vertexBuffer.position(0);

        // 设置顶点属性：位置属性
        // ------------
        attribLocationPosition = GLES20.glGetAttribLocation(programId, "a_Position");
        GLES20.glEnableVertexAttribArray(attribLocationPosition);
        GLES20.glVertexAttribPointer(
                attribLocationPosition,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                3 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexBuffer);

        // 设置fragment shader中的uniform变量：颜色值v_Color
        // --------------
        int colorHandle = GLES20.glGetUniformLocation(programId, "v_Color");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 设置MVP（vertex shader中一个uniform矩阵变量）
        CameraHelper.updateShaderMVP(width, height, programId, angle);

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        // 释放顶点属性
        GLES20.glDisableVertexAttribArray(attribLocationPosition);
    }
}
