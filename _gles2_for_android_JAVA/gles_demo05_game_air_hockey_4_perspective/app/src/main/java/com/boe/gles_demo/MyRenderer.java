package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
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
            // ---X,Y,Z-- --R,G,B---
            // Triangle Fan——即以扇形旋转的方式绘制三角形
            0, 0, 0f, 1f, 1f, 1f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f,
            // 中线
            -0.5f, 0f, 0.05f, 1f, 0f, 0f,
            0.5f, 0f, 0.05f, 1f, 0f, 0f,
            // 两个木槌位置
            0f, -0.4f, 0.05f, 0f, 0f, 1f,
            0f, 0.4f, 0.05f, 1f, 0f, 0f

    };

    private FloatBuffer vertexData = null;

    int programId;
    int uColorLocationHandle;
    int aPositionLocationHandle;
    int aColorLocationHandle;

    private final float[] modelMatrix = new float[16];

    // 投影矩阵
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

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

        // MVP矩阵
        uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix");

        // uniform:u_Color
        uColorLocationHandle = GLES20.glGetUniformLocation(programId, "u_Color");

        // VBO
        // -----------------
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
        vertexData.position(0);

        // Attribute: Position
        aPositionLocationHandle = GLES20.glGetAttribLocation(programId, "a_Position");
        GLES20.glEnableVertexAttribArray(aPositionLocationHandle);
        GLES20.glVertexAttribPointer(
                aPositionLocationHandle,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                6 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

        // Attribute: Color
        aColorLocationHandle = GLES20.glGetAttribLocation(programId, "a_Color");
        GLES20.glEnableVertexAttribArray(aColorLocationHandle);
        vertexData.position(3); // !!! 设置偏移
        GLES20.glVertexAttribPointer(
                aColorLocationHandle,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                6 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);
    }

    // 编译shader，在onSurfaceCreate()中调用
    void loadCompileShaderProgram() {
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.vertex_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.fragment_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogHelper.log("->onSurfaceChanged()");
        sScreenWidth = width;
        sScreenHeight = height;
        GLES20.glViewport(0, 0, width, height);
        applyMVP(0);
    }

    // 透视投影
    void applyMVP(float angle) {
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) sScreenWidth / (float) sScreenHeight, 1f, 10f);

        // Model矩阵变换
        Matrix.setIdentityM(modelMatrix, 0); // Model矩阵初始为单位矩阵
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);

        // 对Model矩阵做旋转 ———— 这个其实就是 View 吧，相当于 V * M 了！！！
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, angle, 1f, 1, 1f);

        // P * M
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        // 传入MVP到Shader
        // uniform: 传入u_Matrix
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    float angle = 0;
    long lastTick = System.currentTimeMillis();

    @Override
    public void onDrawFrame(GL10 gl) {
        LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        GLES20.glUseProgram(programId);

        // 夹带私货，按时间旋转
        if (System.currentTimeMillis() - lastTick > 10) {
            angle -= 0.6;
            lastTick = System.currentTimeMillis();
            applyMVP(angle);
        }

        // 绘制面板 [0-6]点位
        GLES20.glUniform4f(uColorLocationHandle, 0.3f, 0.3f, 0.3f, 1f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

        // 绘制中间分割线
        GLES20.glUniform4f(uColorLocationHandle, 1f, 0f, 0f, 1f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        // 绘制木槌
        GLES20.glUniform4f(uColorLocationHandle, 1f, 1f, 0f, 1f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 4);

    }
}
