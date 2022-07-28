package com.boe.demo2_surface_view_camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SurfaceView用来预览相机
 * <p>
 * 但无法对图像做实时处理
 */
public class MainActivity extends AppCompatActivity {

    static String TAG = "_MainActivity_";
    private Camera mCamera;
    int mCameraId = 1;
    SurfaceView sfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sfView = findViewById(R.id.sfView);
        sfView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    mCamera = Camera.open(0);
                    log("camera is null:" + (mCamera == null));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                startPreview();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }

    void log(String msg) {
        Log.i(TAG, msg);
    }

    void startPreview() {
        if (sfView.getHolder().getSurface() == null) {
            return;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            try {
                mCamera.setPreviewDisplay(sfView.getHolder());
                mCamera.startPreview();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}