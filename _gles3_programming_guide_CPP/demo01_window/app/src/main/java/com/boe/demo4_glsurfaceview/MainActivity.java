package com.boe.demo4_glsurfaceview;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "_MainActivity_";
    static int WIDTH = 600;
    static int HEIGHT = 400;
    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glSurfaceView = findViewById(R.id.glSurfaceView);
        // 创建一个2.0的context
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {

            // 启动（一次）
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
                // 绘制背景色
                GLES20.glClearColor(1f, 0f, 0f, 1);
            }

            // 类似于窗体改变
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0, 0, WIDTH, HEIGHT);
            }

            // 类似于窗体绘制循环
            @Override
            public void onDrawFrame(GL10 gl) {
                log("->onDrawFrame()");
                // 重置颜色、深度
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            }
        });
        // 配合glSurfaceView.requestRender()，调用时才更新
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    void log(String msg) {
        Log.i(TAG, msg);
    }
}