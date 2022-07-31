package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.TypedValue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
            // Triangle 1
            -0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            // Triangle 2
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
    };

    // 中线
    float[] middleLine = {
            // Line 1
            0f, 7f,
            9f, 7f,
    };

    // 两个木槌位置
    float[] mallets = {
            4.5f, 2f,
            4.5f, 12f
    };

    private FloatBuffer vertexData = null;

    int programId;
    int uColorLocationHandle;
    int aPositionLocationHandle;

    public MyRenderer(Context context) {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        sScreenWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300.0f,
                context.getResources().getDisplayMetrics());
        sScreenHeight = sScreenWidth;

        GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        loadCompileShaderProgram();
        GLES20.glUseProgram(programId);

        // 传值给uniform变量
//        uColorLocationHandle = GLES20.glGetUniformLocation(programId, "u_color");
//        GLES20.glUniform4f(uColorLocationHandle, 0f, 1f, 0f, 1f); // Uniform变量设值一般在drawFrame时，因为一般是随帧而变

        // VBO
        // -----------------
        // VertexShader中的a_Position
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
        vertexData.position(0);
        aPositionLocationHandle = GLES20.glGetAttribLocation(programId, "a_Position");
        GLES20.glEnableVertexAttribArray(aPositionLocationHandle);
        GLES20.glVertexAttribPointer(
                aPositionLocationHandle,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                0, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

    }

    // 编译shader，在onSurfaceCreate()中调用
    void loadCompileShaderProgram() {
        // Load And Compile Shaders to Program
        // -----------------------------------
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.vertex_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.fragment_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, sScreenWidth, sScreenHeight);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        GLES20.glUseProgram(programId);
        GLES20.glUniform4f(uColorLocationHandle, 1f, 0f, 0f, 1f);

        // Draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

    }
}
