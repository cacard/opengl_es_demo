package com.boe.gles_demo.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.boe.gles_demo.Constants;
import com.boe.gles_demo.LogHelper;
import com.boe.gles_demo.MatrixHelper;
import com.boe.gles_demo.R;
import com.boe.gles_demo.ShaderHelper;
import com.boe.gles_demo.TextResReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ShapeDivider extends BaseShape {

    Context context;
    int width, height;
    int aPositionLocation;

    float data[] = new float[]{
            -0.5f, 0f, 0.05f,
            0.5f, 0f, 0.05f,
    };

    FloatBuffer vertexData;
    int programId;

    public ShapeDivider(Context context, int width, int height) {
        super(width,height);
        this.context = context;
        this.width = width;
        this.height = height;
        LogHelper.log("=============== ShapeDivider ===============");
        genVBO();
        genProgram();
        bindDataOnDraw();
    }


    void bindDataOnDraw() {

        // Attribute: Position
        // -------------------
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position");
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(
                aPositionLocation,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                0, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

    }

    void genVBO() {
        vertexData = ByteBuffer.allocateDirect(data.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(data);
        vertexData.position(0);
    }

    void genProgram() {
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.line_v_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.line_f_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
    }

    void bindDataMVP() {

        float[] projectionMatrix = new float[16];
        float[] modelMatrix = new float[16];

        // 投影矩阵设置
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

        // Model矩阵变换
        Matrix.setIdentityM(modelMatrix, 0); // Model矩阵初始为单位矩阵
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);
        // 对Model矩阵做旋转 ———— 这个其实就是 View 吧，相当于 V * M 了！！！
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, 0, 1f, 0, 0f);

        // P * M
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        // MVP矩阵
        int uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix");
        // 传入MVP到Shader
        // uniform: 传入u_Matrix
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    public void use() {
        GLES20.glUseProgram(programId);
    }

    @Override
    int getProgramId() {
        return programId;
    }

    public void draw(float angle) {
        use();
        setMVP(angle);
        int uColorLocationHandle = GLES20.glGetUniformLocation(programId, "u_Color");
        GLES20.glUniform4f(uColorLocationHandle, 1f, 0f, 0f, 1f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
    }
}
