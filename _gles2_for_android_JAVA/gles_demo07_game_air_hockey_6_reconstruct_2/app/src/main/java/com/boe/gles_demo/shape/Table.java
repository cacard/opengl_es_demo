package com.boe.gles_demo.shape;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

import com.boe.gles_demo.Constants;
import com.boe.gles_demo.data.VertexArray;
import com.boe.gles_demo.shader.TextureShaderProgram;

public class Table {

    /**
     * 位置信息单元包含几个float数据，一般是3个：x,y,z
     */
    final int positionComponentFloatCount = 3;

    /**
     * 颜色单元包含3个float，由于用到了纹理，所以颜色展示未用
     */
    final int colorComponentFloatCount = 3;

    /**
     * UV信息包含几个float数据，2个
     */
    final int textureCoordinatesComponentFloatCount = 2;

    /**
     * 步长
     */
    final int stride = (positionComponentFloatCount + colorComponentFloatCount + textureCoordinatesComponentFloatCount) * Constants.BYTES_PER_FLOAT;

    static final float[] vertexData = {
            // ---X,Y,Z--, --R,G,B---, --U,V--
            // !!! 这个例子中，RGB已被Texture取代，所以RGB是不需要的，临时放这里也行
            // Triangle Fan——即以扇形旋转的方式绘制三角形
            0f, 0f, 0f, 1f, 1f, 1f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
            0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.9f,
            0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f};

    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(vertexData);
    }

    /**
     * 将缓冲数据绑定到具体的 program 的 顶点属性上
     *
     * @param program
     */
    public void bindData(TextureShaderProgram program) {
        // 绑定到position顶点属性
        int positionLocation = program.getPositionLocation();
        vertexArray.setVertexAttribPointer(0, positionLocation, positionComponentFloatCount, stride);
        // 绑定到uv顶点属性
        int offset = positionComponentFloatCount + colorComponentFloatCount;
        vertexArray.setVertexAttribPointer(offset, program.getTextureCoordinatesLocation(),
                textureCoordinatesComponentFloatCount, stride);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

}
