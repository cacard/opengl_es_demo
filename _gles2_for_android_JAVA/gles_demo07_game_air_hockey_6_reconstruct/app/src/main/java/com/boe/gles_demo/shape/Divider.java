package com.boe.gles_demo.shape;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.CameraHelper;
import com.boe.gles_demo.Constants;
import com.boe.gles_demo.LogHelper;
import com.boe.gles_demo.R;
import com.boe.gles_demo.ShaderHelper;
import com.boe.gles_demo.TextResReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Divider {


    float[] vecList = {
            // ---X,Y,Z--
            // 中线 ——虽然不需要UV，但需要补齐
            -0.5f, 0f, 0.05f,
            0.5f, 0f, 0.05f
    };
    float[] color = {1, 1f, 0.6f, 0.5f};

    Context context;
    private FloatBuffer vertexData = null;
    int programId;
    int attribLocationPosition;

    public Divider(Context context) {
        this.context = context;

        // 准备缓冲数据
        // --------------
        vertexData = ByteBuffer.allocateDirect(vecList.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(vecList);
        vertexData.position(0);

        // 编辑shader
        // ---------------
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.divider_vertex_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.divider_fragment_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);

        LogHelper.log("【Table】programId:" + programId);



    }

    public void draw(float angle, int width, int height) {
        GLES20.glUseProgram(programId);


        vertexData.position(0);

        // Attribute: Position
        int attribLocationPosition = GLES20.glGetAttribLocation(programId, "a_Position");
        LogHelper.log("【Table】aPositionLocationHandle:" + attribLocationPosition);
        GLES20.glEnableVertexAttribArray(attribLocationPosition);
        GLES20.glVertexAttribPointer(
                attribLocationPosition,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                3 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

        // 更新uniform
        // --------------
        int colorHandle = GLES20.glGetUniformLocation(programId, "v_Color");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        CameraHelper.updateShaderMVP(width, height, programId, angle);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        GLES20.glDisableVertexAttribArray(attribLocationPosition);
    }
}
