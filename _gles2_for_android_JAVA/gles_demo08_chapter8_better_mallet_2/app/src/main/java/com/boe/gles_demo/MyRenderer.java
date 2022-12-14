package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.boe.gles_demo.helper.CameraHelper;
import com.boe.gles_demo.helper.LogHelper;
import com.boe.gles_demo.helper.TextureHelper;
import com.boe.gles_demo.shader.ColorShaderProgram;
import com.boe.gles_demo.shader.TextureShaderProgram;
import com.boe.gles_demo.shape.Mallet2;
import com.boe.gles_demo.shape.Puck;
import com.boe.gles_demo.shape.Table;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    Context context;
    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 600;

    Table table;
    Mallet2 mallet;
    Puck puck;
    TextureShaderProgram textureShaderProgram;
    ColorShaderProgram colorShaderProgram;
    int textureId;

    float angle = 0;
    long lastTick = System.currentTimeMillis();
    boolean enableAnim = true;

    public MyRenderer(Context context) {
        this.context = context;
    }

    public void toggleAnim() {
        enableAnim = !enableAnim;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
        GLES20.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogHelper.log("->onSurfaceChanged()");
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        GLES20.glViewport(0, 0, width, height);

        // 创建各个物理对象、program、加载纹理
        // ------------
        table = new Table();
        mallet = new Mallet2(0.1f, 0.3f, 55);
        puck = new Puck(0.1f, 0.06f, 55);
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        textureId = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    /**
     * VP，即：没有model坐标的全局视角坐标
     */
    float[] viewProjectionMatrix = new float[16];

    /**
     * MVP，即：在VP的基础上，对Model坐标做了变换之后的变换矩阵
     */
    float[] modelViewProjectionMatrix = new float[16];

    /**
     * 临时变量，使用时应先重置为单位矩阵
     */
    float[] modelTranslateMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        // 按时间旋转
        if (enableAnim && System.currentTimeMillis() - lastTick > 14) {
            angle -= 0.2;
            lastTick = System.currentTimeMillis();
        }

        // 透视投影变换：全局
        viewProjectionMatrix = CameraHelper.getMVP(SCREEN_WIDTH, SCREEN_HEIGHT, angle);

        // 绘制Table
        // ------------
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniformTexture(textureId);
        textureShaderProgram.setUniformMatrix(viewProjectionMatrix);
        table.bindData(textureShaderProgram);
        table.draw();
        textureShaderProgram.release();


        // 绘制Mallet：两个
        // ------------

        // MVP矩阵：设置初始位置
        float[] mallet1MVP = viewProjectionMatrix.clone();
        Matrix.setIdentityM(modelTranslateMatrix, 0);
        Matrix.rotateM(modelTranslateMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.translateM(modelTranslateMatrix, 0, 0, 0.15f, 0.5f);
        Matrix.multiplyMM(mallet1MVP, 0, mallet1MVP, 0, modelTranslateMatrix, 0);

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniformMatrix(mallet1MVP);
        colorShaderProgram.setUniformColor(0, 1, 0);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        float[] mallet2MVP = viewProjectionMatrix.clone();
        Matrix.setIdentityM(modelTranslateMatrix, 0);
        Matrix.rotateM(modelTranslateMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.translateM(modelTranslateMatrix, 0, 0, 0.15f, -0.5f);
        Matrix.multiplyMM(mallet2MVP, 0, mallet2MVP, 0, modelTranslateMatrix, 0);

        colorShaderProgram.setUniformMatrix(mallet2MVP);
        colorShaderProgram.setUniformColor(0, 0, 1);
        mallet.draw();

        colorShaderProgram.release();

        // 绘制冰球
        // ------------

        // MVP矩阵：设置初始位置
        float[] projectionMatrixPuck = viewProjectionMatrix.clone();
        modelTranslateMatrix = new float[16];
        Matrix.setIdentityM(modelTranslateMatrix, 0);
        Matrix.rotateM(modelTranslateMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.translateM(modelTranslateMatrix, 0, 0f, 0.03f, (float) Math.sin(angle / 10) / 4f);
        Matrix.multiplyMM(projectionMatrixPuck, 0, projectionMatrixPuck, 0, modelTranslateMatrix, 0);

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniformMatrix(projectionMatrixPuck);
        colorShaderProgram.setUniformColor(1, 1, 0);
        puck.bindData(colorShaderProgram);
        puck.draw();
        colorShaderProgram.release();
    }

}
