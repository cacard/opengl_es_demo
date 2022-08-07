package com.boe.gles_demo.shader;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.R;
import com.boe.gles_demo.helper.LogHelper;

/**
 * 颜色值渲染
 *
 * - MVP
 * - 输入颜色
 */
public class ColorShaderProgram extends ShaderProgram {

    // 顶点属性
    // ------------
    private final int aPositionLocation;

    // 通过顶点属性传入color
    private final int aColorLocation;

    // unifrom 传入 color
    private final int uColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        if (aPositionLocation < 0) {
            LogHelper.logE("【@ColorShaderProgram】aPositionLocation=" + aPositionLocation);
        }

        aColorLocation = glGetAttribLocation(program, A_COLOR);
        if (aColorLocation < 0) {
            // !!! 一般-1的情况是，由于值未用到，被优化掉了！
            LogHelper.logE("【@ColorShaderProgram】aColorLocation=" + aColorLocation);
        }

        uColorLocation = glGetUniformLocation(program, U_COLOR);
        if (uColorLocation < 0) {
            LogHelper.logE("【@ColorShaderProgram】uColorLocation=" + uColorLocation);
        }
    }

    public void setUniformColor(float r, float g, float b) {
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionLocation() {
        return aPositionLocation;
    }

    public int getColorLocation() {
        return aColorLocation;
    }

    @Override
    public void release() {
        super.release();
        if (aPositionLocation >= 0) {
            GLES20.glDisableVertexAttribArray(aPositionLocation);
        }
        if (aColorLocation >= 0) {
            GLES20.glDisableVertexAttribArray(aColorLocation);
        }

    }
}
