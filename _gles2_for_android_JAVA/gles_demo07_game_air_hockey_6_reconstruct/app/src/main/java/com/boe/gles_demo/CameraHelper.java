package com.boe.gles_demo;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class CameraHelper {

    static void updateShaderMVP(int screenWidth, int screenHeight, int programId, float angle) {

        final float[] projectionMatrix = new float[16];
        final float[] modelMatrix = new float[16];
        int uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix");

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) screenWidth / (float) screenHeight, 1f, 10f);

        // Model矩阵变换
        Matrix.setIdentityM(modelMatrix, 0); // Model矩阵初始为单位矩阵
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);

        // 对Model矩阵做旋转 ———— 这个其实就是 View 吧，相当于 V * M 了！！！
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, angle, 1f, 1, 1f);

        // P * M
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        // 传入MVP到Shader
        // uniform: 传入u_Matrix
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

}
