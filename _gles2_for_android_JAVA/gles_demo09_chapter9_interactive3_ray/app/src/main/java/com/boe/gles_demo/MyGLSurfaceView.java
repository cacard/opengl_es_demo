package com.boe.gles_demo;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;

public class MyGLSurfaceView extends GLSurfaceView {

    MyRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {

        // 创建一个2.0的context
        setEGLContextClientVersion(2);

        // 创建Renderer
        renderer = new MyRenderer(getContext());
        setRenderer(renderer);

        // 根据情况设定：仅当手动调用request时才刷新
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Debug日志
        setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS);

        initListener();
    }

    void initListener() {
        this.post(new Runnable() {
            @Override
            public void run() {
                ((Activity) getContext()).findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //renderer.toggleAnim();
                    }
                });
            }
        });
    }
}
