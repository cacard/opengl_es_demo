package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.boe.gles_demo.helper.CameraHelper;
import com.boe.gles_demo.helper.LogHelper;
import com.boe.gles_demo.helper.TextureHelper;
import com.boe.gles_demo.shader.ColorShaderProgram;
import com.boe.gles_demo.shader.TextureShaderProgram;
import com.boe.gles_demo.shape.Mallet;
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
        mallet = new Mallet2(0.1f, 0.3f, 15);
        puck = new Puck(0.2f, 0.1f, 15);
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        textureId = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        // 按时间旋转
        if (enableAnim && System.currentTimeMillis() - lastTick > 10) {
            angle -= 0.6;
            lastTick = System.currentTimeMillis();
        }

        // 透视投影变换
        float[] projectionMatrix = CameraHelper.getMVP(SCREEN_WIDTH, SCREEN_HEIGHT, angle);

        // 绘制Table
        // ------------
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniformTexture(textureId);
        textureShaderProgram.setUniformMatrix(projectionMatrix);
        table.bindData(textureShaderProgram);
        table.draw();
        textureShaderProgram.release();

        // 绘制Mallet
        // ------------
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniformMatrix(projectionMatrix);
        colorShaderProgram.setUniformColor(0, 1, 0);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
        colorShaderProgram.release();

        // 绘制冰球
        // ------------
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniformMatrix(projectionMatrix);
        puck.bindData(colorShaderProgram);
        puck.draw();
        colorShaderProgram.release();
    }
}
