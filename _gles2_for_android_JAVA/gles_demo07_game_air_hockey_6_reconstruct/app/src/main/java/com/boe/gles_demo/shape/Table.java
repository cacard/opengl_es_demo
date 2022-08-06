package com.boe.gles_demo.shape;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.CameraHelper;
import com.boe.gles_demo.Constants;
import com.boe.gles_demo.LogHelper;
import com.boe.gles_demo.R;
import com.boe.gles_demo.ShaderHelper;
import com.boe.gles_demo.TextResReader;
import com.boe.gles_demo.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Table {

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

    };

    Context context;


    private FloatBuffer vertexData = null;
    int programId;
    int textureId;
    int attribLocationPosition;
    int attribLocationUV;

    public Table(Context context) {
        this.context = context;


        // 准备缓冲数据
        // --------------
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
        vertexData.position(0);


//        IntBuffer buffer = IntBuffer.allocate(1);
//        GLES20.glGenBuffers(1, buffer);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.get(0));
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, tableVerticesWithTriangles.length * Constants.BYTES_PER_FLOAT, vertexData, GLES20.GL_STATIC_DRAW);

        // 编辑shader
        // ---------------
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_vertex_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_fragment_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
        LogHelper.log("【Table】programId:" + programId);

        // Texture
        // ----------------
        textureId = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);



        // 为program中变量赋值
        // 注意的是，这些变量赋值是一次性的，所以放到构造函数里面


        GLES20.glUseProgram(programId);



        // Attribute: Position
        attribLocationPosition = GLES20.glGetAttribLocation(programId, "a_Position");
        LogHelper.log("【Table】aPositionLocationHandle:" + attribLocationPosition);
        GLES20.glEnableVertexAttribArray(attribLocationPosition);
        GLES20.glVertexAttribPointer(
                attribLocationPosition,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

        // Attribute: UV
        attribLocationUV = GLES20.glGetAttribLocation(programId, "a_UV");
        GLES20.glEnableVertexAttribArray(attribLocationUV);
        vertexData.position(6); // !!! 设置偏移
        GLES20.glVertexAttribPointer(
                attribLocationUV,
                2, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

    }



    public void draw(float angle, int width, int height) {


        // 赋值纹理 uniform:u_Texture
        // ----------------
        int uniformTexture = GLES20.glGetUniformLocation(programId, "u_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //激活0号纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); //绑定textureId
        GLES20.glUniform1i(uniformTexture, 0); //传入数据0（意思是0号纹理）给unifrom值

        CameraHelper.updateShaderMVP(width, height, programId, angle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

    }
}
