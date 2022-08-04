//package com.boe.gles_demo;
//
//import android.content.Context;
//import android.opengl.GLES20;
//import android.opengl.Matrix;
//
//public class CameraManager {
//
//    Context context;
//    int width;
//    int height;
//
//    public CameraManager(Context context, int width, int height) {
//        this.context = context;
//        this.width = width;
//        this.height = height;
//    }
//
//    void bindDataMVP(float angle) {
//
//        float[] projectionMatrix = new float[16];
//        float[] modelMatrix = new float[16];
//
//        // 投影矩阵设置
//        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//
//        // Model矩阵变换
//        Matrix.setIdentityM(modelMatrix, 0); // Model矩阵初始为单位矩阵
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);
//        // 对Model矩阵做旋转 ———— 这个其实就是 View 吧，相当于 V * M 了！！！
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
//        Matrix.rotateM(modelMatrix, 0, angle, 1f, 0, 0f);
//
//        // P * M
//        final float[] temp = new float[16];
//        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
//
//        // MVP矩阵
//        int uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix");
//        // 传入MVP到Shader
//        // uniform: 传入u_Matrix
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
//    }
//}
