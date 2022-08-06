package com.boe.gles_demo.shape;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.helper.CameraHelper;
import com.boe.gles_demo.Constants;
import com.boe.gles_demo.helper.LogHelper;
import com.boe.gles_demo.R;
import com.boe.gles_demo.helper.ShaderHelper;
import com.boe.gles_demo.helper.TextResReader;
import com.boe.gles_demo.helper.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 桌面
 */
public class Table {

    // 桌子顶点（两个三角形分开）——CCW（Counter CLock Wise Order）
    // Triangle Fan——即以扇形旋转的方式绘制三角形
    float[] vertexes = {
            // ---X,Y,Z--, --R,G,B---, --U,V--
            // !!! 这个例子中，RGB已被Texture取代，所以RGB是不需要的，临时放这里也行
            0f, 0f, 0f, 1f, 1f, 1f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
            0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.9f,
            0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
    };

    Context context;

    private FloatBuffer vertexBuffer;
    int programId;
    int textureId;

    // 顶点属性
    // ------------
    int attribLocationPosition;
    int attribLocationUV;

    public Table(Context context) {
        this.context = context;

        // 准备缓冲数据
        // --------------
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertexes.length * Constants.BYTES_PER_FLOAT);
        buffer = buffer.order(ByteOrder.nativeOrder());
        vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(vertexes);
        vertexBuffer.position(0);

        // 编译shader
        // ---------------
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_vertex_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_fragment_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
        LogHelper.log("【Table】programId:" + programId);

        // 加载Texture
        // ----------------
        textureId = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

    }


    public void draw(float angle, int width, int height) {
        GLES20.glUseProgram(programId);

        // 重置buffer的position
        vertexBuffer.position(0);

        // 设置顶点属性
        // ------------

        // Attribute: Position
        attribLocationPosition = GLES20.glGetAttribLocation(programId, "a_Position");
        LogHelper.log("【Table】aPositionLocationHandle:" + attribLocationPosition);
        GLES20.glEnableVertexAttribArray(attribLocationPosition);
        GLES20.glVertexAttribPointer(
                attribLocationPosition,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexBuffer);

        // Attribute: UV
        attribLocationUV = GLES20.glGetAttribLocation(programId, "a_UV");
        GLES20.glEnableVertexAttribArray(attribLocationUV);
        vertexBuffer.position(6); // !!! 设置偏移
        GLES20.glVertexAttribPointer(
                attribLocationUV,
                2, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexBuffer);


        // 赋值纹理 uniform:u_Texture
        // ----------------
        int uniformTexture = GLES20.glGetUniformLocation(programId, "u_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //激活0号纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); //绑定textureId
        GLES20.glUniform1i(uniformTexture, 0); //传入数据0（意思是0号纹理）给unifrom值

        // 设置MVP
        CameraHelper.updateShaderMVP(width, height, programId, angle);

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

        // 释放顶点属性
        GLES20.glDisableVertexAttribArray(attribLocationPosition);
        GLES20.glDisableVertexAttribArray(attribLocationUV);
    }
}
