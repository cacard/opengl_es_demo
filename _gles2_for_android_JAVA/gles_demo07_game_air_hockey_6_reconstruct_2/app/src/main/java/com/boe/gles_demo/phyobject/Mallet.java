package com.boe.gles_demo.phyobject;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

import com.boe.gles_demo.Constants;
import com.boe.gles_demo.data.VertexArray;
import com.boe.gles_demo.shader.ColorShaderProgram;

public class Mallet {

    /**
     * 位置信息单元包含几个float数据，一般是3个：x,y,z
     */
    final int positionComponentFloatCount = 3;

    /**
     * Color信息包含几个float数据，3个
     */
    final int colorComponentFloatCount = 3;

    /**
     * 步长
     */
    final int stride = (positionComponentFloatCount + colorComponentFloatCount) * Constants.BYTES_PER_FLOAT;

    static final float[] vertexData = {
            // ---X,Y,Z--, --R,G,B---
            // 两个木槌位置
            0f, -0.4f, 0.05f, 0f, 0f, 1f,
            0f, 0.4f, 0.05f, 1f, 0f, 0f};

    private final VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(vertexData);
    }

    /**
     * 将缓冲数据绑定到具体的 program 的 顶点属性上
     *
     * @param program
     */
    public void bindData(ColorShaderProgram program) {
        // 顶点属性:Position
        int positionLocation = program.getPositionLocation();
        vertexArray.setVertexAttribPointer(0, positionLocation, positionComponentFloatCount, stride);
        // 顶点属性:Color
        int colorLocation = program.getColorLocation();
        vertexArray.setVertexAttribPointer(positionComponentFloatCount, colorLocation, colorComponentFloatCount, stride);
    }

    public void draw() {
        vertexArray.resetPosition();
        glDrawArrays(GL_POINTS, 0, 2);
    }
}
