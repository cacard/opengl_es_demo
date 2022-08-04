package com.boe.gles_demo.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.boe.gles_demo.Constants;
import com.boe.gles_demo.LogHelper;
import com.boe.gles_demo.MatrixHelper;
import com.boe.gles_demo.R;
import com.boe.gles_demo.ShaderHelper;
import com.boe.gles_demo.TextResReader;
import com.boe.gles_demo.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TableShape extends BaseShape {

    Context context;
    int width, height;

    float[] data = {
            // ---X,Y,Z--, --R,G,B---, --U,V--
            // !!! 这个例子中，RGB已被Texture取代，所以RGB是不需要的，临时放这里也行
            // Triangle Fan——即以扇形旋转的方式绘制三角形
            0f, 0f, 0f, 1f, 1f, 1f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
            0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.9f,
            0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f

    };

    private FloatBuffer vertexData = null;
    int programId;
    int textureId; //Texture id
    float angle = -70;

    public TableShape(Context context, int width, int height) {
        super(width, height);
        this.context = context;
        this.width = width;
        this.height = height;
        LogHelper.log("=============== ShapeTable ===============");
        genVBO();
        genProgram();
        use();
        bindData();
    }

    void genVBO() {
        vertexData = ByteBuffer.allocateDirect(data.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(data);
        vertexData.position(0);
    }

    void genProgram() {
        String vertexShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_v_shader);
        String fragmentShaderSource = TextResReader.readTextFileFromResource(context, R.raw.table_f_shader);
        programId = ShaderHelper.linkProgram(vertexShaderSource, fragmentShaderSource);
    }

    int aPositionLocation;
    int aColorLocationHandle;
    int aUVHandle;

    void bindData() {

        // Attribute: Position
        // -------------------
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position");
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(
                aPositionLocation,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

        // Attribute: Color
        // 由于下面使用了Texture，所以这个颜色其实用不到了
        // ----------------
        aColorLocationHandle = GLES20.glGetAttribLocation(programId, "a_Color");
        GLES20.glEnableVertexAttribArray(aColorLocationHandle);
        vertexData.position(3); // !!! 设置偏移
        GLES20.glVertexAttribPointer(
                aColorLocationHandle,
                3, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

        // Attribute: UV
        // ----------------
        aUVHandle = GLES20.glGetAttribLocation(programId, "a_UV");
        GLES20.glEnableVertexAttribArray(aUVHandle);
        vertexData.position(6); // !!! 设置偏移
        GLES20.glVertexAttribPointer(
                aUVHandle,
                2, //每个顶点的数据量（一个顶点3个float数据）
                GLES20.GL_FLOAT,
                false,
                8 * Constants.SIZE_OF_FLOAT, // OR: 3 * SIZE_OF_FLOAT
                vertexData);

        // Texture
        // ----------------
        textureId = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        LogHelper.log("textureId:" + String.valueOf(textureId));

    }



    public void use() {
        GLES20.glUseProgram(programId);
    }

    @Override
    int getProgramId() {
        return programId;
    }

    public void draw(float angle) {
        GLES20.glUseProgram(programId);

        setMVP(angle);

        // 面板纹理，uniform:u_Texture
        int uTextureHandle = GLES20.glGetUniformLocation(programId, "u_Texture");
        // 纹理传入的是X号纹理，需要先激活0号，再绑定，再传入数据
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //激活0号纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); //绑定textureId
        GLES20.glUniform1i(uTextureHandle, 0); //传入数据0（意思是0号纹理）给unifrom值

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);



    }

}
