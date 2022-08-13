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

        // 透视矩阵
        final float[] projectionMatrix = new float[16];
        // 透视矩阵：near1, far10
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) screenWidth / (float) screenHeight, 0.001f, 5f);

        // Model矩阵变换
        final float[] modelMatrix = new float[16];
        // Model矩阵初始为单位矩阵
        Matrix.setIdentityM(modelMatrix, 0);
        // 因为透视的near是1，所以，这里需要将模型移动到平接头体里
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
        // 旋转模型
        Matrix.rotateM(modelMatrix, 0, angle, 1f, 0f, 0f);

        // P * (V) * M
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        return projectionMatrix;
    }

}
