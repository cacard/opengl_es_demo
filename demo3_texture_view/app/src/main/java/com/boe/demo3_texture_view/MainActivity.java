package com.boe.demo3_texture_view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static String TAG = "_MainActivity_";
    TextureView textureView;

    void log(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.textureView);

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                log("onSurfaceTextureAvailable");

                // surfaceTexture有个getTransformMatrix，是4x4的矩阵，
                // 获取一下看看，是默认的单位矩阵：1.0/0.0/0.0/0.0/0.0/1.0/0.0/0.0/0.0/0.0/1.0/0.0/0.0/0.0/0.0/1.0/
                // 【矩阵的含义】This matrix transforms traditional 2D OpenGL ES texture coordinate column vectors of the form (s, t, 0, 1) where s and t are on the inclusive interval [0, 1] to the proper sampling location in the streamed texture.  This transform compensates for any properties of the image stream source that cause it to appear different from a traditional OpenGL ES texture.  For example, sampling from the bottom left corner of the image can be accomplished by transforming the column vector (0, 0, 0, 1) using the queried matrix, while sampling from the top right corner of the image can be done by transforming (1, 1, 0, 1).
                // -------------------------------------------------
                float[] mtx = new float[16];
                surface.getTransformMatrix(mtx);
                String mtxStr = "";
                for (float f : mtx) {
                    mtxStr += String.valueOf(f) + "/";
                }
                log("getTransformMatrix:" + mtxStr);

                // 绘制
                // 与SurfaceView相同，可以得到一个Canvas，进行绘制
                // ----
                Canvas canvas = textureView.lockCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setTextSize(32);
                canvas.drawText("hello", 100, 100, paint);
                textureView.unlockCanvasAndPost(canvas);

                // 更新transformMatrix
                // --------------------
                Matrix matrixGet = new Matrix();
                matrixGet = textureView.getTransform(matrixGet);
                log("textureView.getTransform():" + matrixGet.toString());
                Matrix matrix = new Matrix();
                ImageHelper.changeMatrix(1080, 700, matrix, 0.3f);
                textureView.setTransform(matrix);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                log("onSurfaceTextureSizeChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                log("onSurfaceTextureUpdated");
            }
        });

    }
}