package com.boe.gles_demo;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.boe.gles_demo.helper.LogHelper;
import com.boe.gles_demo.shape.Divider;
import com.boe.gles_demo.shape.Handle;
import com.boe.gles_demo.shape.Table;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {

    Context context;

    int SCREEN_WIDTH = 800;
    int SCREEN_HEIGHT = 300;

    Table table;
    Divider divider;
    Handle handle;

    float angle = 0;
    long lastTick = System.currentTimeMillis();
    boolean enableAnim = true;

    public MyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enableAnim = !enableAnim;
                    }
                });
            }
        });

        GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
        GLES20.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogHelper.log("->onSurfaceChanged()");
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        GLES20.glViewport(0, 0, width, height);
        table = new Table(context);
        divider = new Divider(context);
        handle = new Handle(context);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // 按时间旋转
        if (enableAnim && System.currentTimeMillis() - lastTick > 10) {
            angle -= 0.6;
            lastTick = System.currentTimeMillis();
        }
        table.draw(angle, SCREEN_WIDTH, SCREEN_HEIGHT);
        divider.draw(angle, SCREEN_WIDTH, SCREEN_HEIGHT);
        handle.draw(angle, SCREEN_WIDTH, SCREEN_HEIGHT);
    }
}
