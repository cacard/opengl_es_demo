package com.boe.gles_demo.shader;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

import android.content.Context;

import com.boe.gles_demo.helper.ShaderHelper;
import com.boe.gles_demo.helper.TextResReader;

public class ShaderProgram {

    // ================================= 顶点属性常量 ==============================

    /**
     * 顶点属性：位置
     */
    protected static final String A_POSITION = "a_Position";

    /**
     * 顶点属性：颜色
     */
    protected static final String A_COLOR = "a_Color";

    /**
     * 顶点属性：UV
     */
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    /**
     * 顶点属性：DirectionVector
     */
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";

    /**
     * 顶点属性：ParticleStartTime
     */
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // ================================= uniform 名称常量 ==============================

    /**
     * MVP矩阵 Uniform 变量名
     */
    protected static final String U_MATRIX = "u_Matrix";

    /**
     * 纹理 uniform 变量名
     */
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    /**
     * 通过unifrom传入颜色值，用于那种不是通过顶点属性传入颜色值的场景
     */
    protected static final String U_COLOR = "u_Color";

    protected static final String U_TIME = "u_Time";

    // ================================= 共用变量 ==============================

    /**
     * program id
     */
    protected int program;

    /**
     * MVP矩阵
     */
    protected int uMatrixLocation;

    /**
     * 构造函数
     * <p>
     * 已获取到的变量包括
     * - U_MATRIX
     */
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
