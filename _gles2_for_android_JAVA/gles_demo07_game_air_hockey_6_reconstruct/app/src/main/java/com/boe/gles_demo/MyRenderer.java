package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.TypedValue;

import com.boe.gles_demo.shape.ShapeDivider;
import com.boe.gles_demo.shape.TableShape;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {

    Context context;

    int sScreenWidth = 1;
    int sScreenHeight = 1;

    // 桌子顶点（4顶点的四边形）
    float[] tableVertices = {
            0f, 0f,
            0f, 14f,
            9f, 14f,
            9f, 0f
    };

    // 桌子顶点（两个三角形分开）——CCW（Counter CLock Wise Order）
    // !!! 我这里跟教程不同的是，我每个顶点用了x,y,z三个
    float[] tableVerticesWithTriangles = {
            // ---X,Y,Z--, --R,G,B---, --U,V--
            // !!! 这个例子中，RGB已被Texture取代，所以RGB是不需要的，临时放这里也行
            // Triangle Fan——即以扇形旋转的方式绘制三角形
            0f, 0f, 0f, 1f, 1f, 1f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
            0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.9f,
            0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
            // 中线 ——虽然不需要UV，但需要补齐
            -0.5f, 0f, 0.05f, 1f, 0f, 0f, 0f, 0f,
            0.5f, 0f, 0.05f, 1f, 0f, 0f, 0f, 0f,
            // 两个木槌位置 ——虽然不需要UV，但需要补齐
            0f, -0.4f, 0.05f, 0f, 0f, 1f, 0f, 0f,
            0f, 0.4f, 0.05f, 1f, 0f, 0f, 0f, 0f,

    };

    private FloatBuffer vertexData = null;

    int programId;
    int uColorLocationHandle;
    int aPositionLocationHandle;
    int aColorLocationHandle;

    int aUVHandle; //UV句柄
    int uTextureHandle; //Texture句柄
    int textureId; //Texture id

    private final float[] modelMatrix = new float[16];

    // 投影矩阵
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    TableShape table;
    ShapeDivider divider;

    public MyRenderer(Context context) {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

//        ((Activity) context).findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                enableAnim = !enableAnim;
//            }
//        });

        sScreenWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300.0f,
                context.getResources().getDisplayMetrics());
        sScreenHeight = sScreenWidth;

        GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
        GLES20.glClearColor(0f, 0f, 0f, 1f);



    }

    // 编译shader，在onSurfaceCreate()中调用
    void loadCompileShaderProgram() {
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_v_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_f_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogHelper.log("->onSurfaceChanged()");
        sScreenWidth = width;
        sScreenHeight = height;
        GLES20.glViewport(0, 0, width, height);
        table = new TableShape(context, sScreenWidth, sScreenHeight);
        divider = new ShapeDivider(context, sScreenWidth, sScreenHeight);
    }



    float angle = 0;
    long lastTick = System.currentTimeMillis();
    boolean enableAnim = true;

    @Override
    public void onDrawFrame(GL10 gl) {
        LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);


        float angle = -60f;

//        table.draw(angle);


        divider.draw(angle);


//        // 绘制面板 [0-6]点位
//        GLES20.glUniform4f(uColorLocationHandle, 0.3f, 0.3f, 0.3f, 1f);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
//
//        // 面板纹理，uniform:u_Texture
//        uTextureHandle = GLES20.glGetUniformLocation(programId, "u_Texture");
//        // 纹理传入的是X号纹理，需要先激活0号，再绑定，再传入数据
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //激活0号纹理
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); //绑定textureId
//        GLES20.glUniform1i(uTextureHandle, 0); //传入数据0（意思是0号纹理）给unifrom值
//
//        // 绘制中间分割线
//        GLES20.glUniform4f(uColorLocationHandle, 1f, 0f, 0f, 1f);
//        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
//
//        // 绘制木槌
//        GLES20.glUniform4f(uColorLocationHandle, 1f, 1f, 0f, 1f);
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 4);

    }
}
