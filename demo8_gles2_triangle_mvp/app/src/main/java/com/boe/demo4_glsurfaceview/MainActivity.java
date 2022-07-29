package com.boe.demo4_glsurfaceview;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "_MainActivity_";
    int WIDTH = 200;
    int HEIGHT = 200;
    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300.0f, getResources().getDisplayMetrics());
        HEIGHT = WIDTH;

        glSurfaceView = findViewById(R.id.glSurfaceView);
        // 创建一个2.0的context
        glSurfaceView.setEGLContextClientVersion(2);


        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {

            TriangleSimple triangleSimple;
            Triangle triangle;

            // vPMatrix is an abbreviation for "Model View Projection Matrix"
            private final float[] vPMatrix = new float[16];
            // 透视矩阵
            private final float[] projectionMatrix = new float[16];
            private final float[] viewMatrix = new float[16];

            long lastTick = System.currentTimeMillis();

            // 启动（一次）
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
                // 绘制背景色
                GLES20.glClearColor(0f, 1f, 0.2f, 1);
                triangleSimple = new TriangleSimple();
                triangle = new Triangle();
            }

            // 类似于窗体改变
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                // 设置窗口大小
                GLES20.glViewport(0, 0, WIDTH, HEIGHT);
                // 窗体宽高比
                float r = (float) WIDTH / HEIGHT;
                // 透视矩阵
                Matrix.frustumM(
                        projectionMatrix,
                        0,
                        -r, //left: near面的left
                        r, //right:near面的right
                        -1, //near面的bottom
                        1, //near面的top
                        3, //new面的距离
                        7 //far面的距离
                );

                // 摄像机位置
                Matrix.setLookAtM(viewMatrix,
                        0, 0, 0, 3,
                        0f, 0f, 0f,
                        0f, 1.0f, 0.0f);

                // P*V
                Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            }

            // 类似于窗体绘制循环
            @Override
            public void onDrawFrame(GL10 gl) {
                log("->onDrawFrame()");
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                // 比较有MVP和MVP的绘制，当前配置下，无区别
                if (System.currentTimeMillis() - lastTick > 1000) {
                    triangleSimple.draw();
                    lastTick = System.currentTimeMillis();
                } else {
                    triangle.draw(vPMatrix);
                }

                glSurfaceView.requestRender();
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