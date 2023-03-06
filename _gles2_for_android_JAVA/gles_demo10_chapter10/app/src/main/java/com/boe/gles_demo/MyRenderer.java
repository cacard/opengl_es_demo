package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.boe.gles_demo.helper.CameraHelper;
import com.boe.gles_demo.helper.LogHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    Context context;
    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 600;

    float angleDefault = -0f;
    float angle = angleDefault;
    long lastTick = System.currentTimeMillis();
    boolean enableAnim = false;


    // 所有物体都具有的基础MVP
    float[] projectionMatrix = new float[16];
    // 在基础MVP的基础上对Model做二次变换时的临时变量
    float[] tempModelMatrix = new float[16];


    public MyRenderer(Context context) {
        this.context = context;
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

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        // 按时间旋转
        if (enableAnim && System.currentTimeMillis() - lastTick > 14) {
            angle -= 0.2;
            lastTick = System.currentTimeMillis();
            if (angle < -120) {
                angle = angleDefault;
                enableAnim = false;
            }
        }

        // 透视投影变换：全局
        projectionMatrix = CameraHelper.getMVP(SCREEN_WIDTH, SCREEN_HEIGHT, angle);

    }


}
