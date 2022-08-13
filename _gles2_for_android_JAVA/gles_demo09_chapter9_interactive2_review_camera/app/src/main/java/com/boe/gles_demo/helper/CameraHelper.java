package com.boe.gles_demo.helper;

import android.opengl.Matrix;

public class CameraHelper {

    /**
     * 获取变换MVP矩阵
     *
     * @param screenWidth
     * @param screenHeight
     * @param angle
     * @return
     */
    public static float[] getMVP(int screenWidth, int screenHeight, float angle) {

        final float[] projectionMatrix = new float[16];
        final float[] modelMatrix = new float[16];

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) screenWidth / (float) screenHeight, 1f, 10f);

        // Model矩阵变换
        Matrix.setIdentityM(modelMatrix, 0); // Model矩阵初始为单位矩阵
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -1f);

        // 对Model矩阵做旋转 ———— 这个其实就是 View 吧，相当于 V * M 了！！！
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);
        Matrix.rotateM(modelMatrix, 0, angle, 1f, 1f, 1f);

        // P * M
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        return projectionMatrix;
    }

}
