package com.boe.gles_demo.shader;

import android.content.Context;

import com.boe.gles_demo.helper.ShaderHelper;
import com.boe.gles_demo.helper.TextResReader;
import static android.opengl.GLES20.*;

public class ShaderProgram {

    /**
     * MVP矩阵 Uniform 变量名
     */
    protected static final String U_MATRIX = "u_Matrix";

    /**
     * 纹理 uniform 变量名
     */
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    /**
     * 顶点属性：位置
     */
    protected static final String A_POSITION = "a_Position";

    /**
     * 顶点属性：颜色
     */
    protected static final String A_COLOR = "a_Color";

    /**
     * 通过unifrom传入颜色值，用于那种不是通过顶点属性传入颜色值的场景
     */
    protected static final String U_COLOR = "u_Color";

    /**
     * 顶点属性：UV
     */
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";


    /**
     * program id
     */
    protected int program;

    /**
     * MVP矩阵
     */
    protected int uMatrixLocation;

    public ShaderProgram(Context context, int vertexShaderResId, int fragmentShaderResId) {
        String vShader = TextResReader.readTextFileFromResource(context, vertexShaderResId);
        String fShader = TextResReader.readTextFileFromResource(context, fragmentShaderResId);
        program = ShaderHelper.linkProgram(vShader, fShader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
    }

    /**
     * 设置MVP变量值
     *
     * @param matrix MVP矩阵
     */
    public void setUniformMatrix(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public void useProgram() {
        glUseProgram(program);
    }

    public void release() {

    }
}
