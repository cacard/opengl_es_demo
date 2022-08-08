package com.boe.gles_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    MyGLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.glSurfaceView);

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float normalizedX =
                        (event.getX() / (float) v.getWidth()) * 2 - 1;
                final float normalizedY =
                        -((event.getY() / (float) v.getHeight()) * 2 - 1);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // queueEvent进行线程之间通信
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            glSurfaceView.renderer.handleTouchPress(normalizedX, normalizedY);
                        }
                    });
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    glSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            glSurfaceView.renderer.handleTouchDrag(normalizedX, normalizedY);
                        }
                    });
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}