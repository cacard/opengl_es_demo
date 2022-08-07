package com.boe.gles_demo.data;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import com.boe.gles_demo.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 顶点数据类
 *
 * - 放置缓冲数据
 * - 设置顶点属性
 */
public class VertexArray {

    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT);
        buffer = buffer.order(ByteOrder.nativeOrder());
        floatBuffer = buffer.asFloatBuffer();
        floatBuffer.put(vertexData);
    }

    /**
     * 设置顶点属性
     *
     * @param dataOffset     偏移
     * @param attribLocation program拿到的handle
     * @param componentCount 对应glVertexAttribPointer的size参数，每个顶点多少个float数据
     * @param stride         步长
     */
    public void setVertexAttribPointer(int dataOffset, int attribLocation, int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attribLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attribLocation);
        floatBuffer.position(0);
    }

    public void resetPosition() {
        floatBuffer.position(0);
    }
}
