package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

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

        renderer = new MyRenderer(getContext());
        setRenderer(renderer);

        // !!! 根据情况设定
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS);
    }
}
