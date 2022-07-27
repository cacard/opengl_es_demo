package com.boe.demo1_surface_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * SurfaceView研究
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    static final String TAG = "_MainActivity_";
    SurfaceView mySurfaceView;

    void log(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySurfaceView = findViewById(R.id.mySurfaceView);

        // 默认有一个SurfaceHolder
        mySurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            Paint paint = new Paint();

            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                log("->surfaceCreated");
                printThread();
                paint.setColor(Color.RED);
                paint.setTextSize(50);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                log("->surfaceChanged");
                printThread();
                Canvas canvas = holder.lockCanvas();
                canvas.drawText("hello", 100, 100, paint);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                log("->surfaceDestroyed");
            }
        });
    }

    void printThread() {
        log("thread:" + Thread.currentThread().getName());
    }
}