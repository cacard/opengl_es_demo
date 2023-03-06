package com.boe.demo4_glsurfaceview;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    // 顶点Shader
    private static final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 a_UV; // Texture UV\n"+
                    "varying vec2 v_UV;\n"+
                    "void main() {\n" +
                    "   v_UV = a_UV;\n"+
                    "  gl_Position = vPosition;\n" +
                    "}\n";

    // Fragment Shader
    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "uniform sampler2D u_Texture;\n"+
                    "varying vec2 v_UV;\n"+
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(u_Texture, v_UV);\n" +
                    "}\n";

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;
    private ShortBuffer drawListBuffer;

    static final int COORDS_PER_VERTEX = 3;

    private static final float sScale = 0.1f;

    private int mTextureId;

    // 顶点：不重复
    static float squareCoords[] = {
            -1f * sScale, 1f * sScale, 0.0f,   // top left
            -1f * sScale, -1f * sScale, 0.0f,   // bottom left
            1f * sScale, -1f * sScale, 0.0f,   // bottom right
            1f * sScale, 1f * sScale, 0.0f}; // top right


    static float uv[] = {
            1.0f , 1.0f ,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };

    // 顶点索引
    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    float[] color = {1, 0.1f, 0.6f, 1f};
    int program;

    public Square(Context context) {
        // VBO
        // ------------
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(uv.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uv);
        uvBuffer.position(0);

        // EBO?
        //-------------
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // 编译Shader/Program
        program = ShaderHelper.linkProgram(vertexShaderCode, fragmentShaderCode);

        // load texture
        // --------------
        mTextureId = TextureHelper.loadTexture(context, R.drawable.gaze_05, true);
    }

    public void draw() {
        GLES20.glUseProgram(program);

        // 顶点属性：位置
        // --------------
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, //index:
                3, //size:
                GLES20.GL_FLOAT,
                false,
                3 * 4, //stride:步长
                vertexBuffer //数据
        );

        // Attr:a_UV
        final int uvHandle = GLES20.glGetAttribLocation(program, "a_UV");
        GLES20.glEnableVertexAttribArray(uvHandle);
        GLES20.glVertexAttribPointer(
                uvHandle, //index:
                2, //size:
                GLES20.GL_FLOAT,
                false,
                2 * 4, //stride:步长
                uvBuffer //数据
        );

        // 更新uniform
        // --------------
        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // update uniform:a_Texture
        // todo:无需直接更新？直接激活并使用？那多个呢？
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        // 绘制
        // 这里使用了drawElements，就是按EBO绘制

        //支持透明
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,  //EBO的长度
                GLES20.GL_UNSIGNED_SHORT, //!EBO的类型
                drawListBuffer //EBO
        );

        // 释放
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
