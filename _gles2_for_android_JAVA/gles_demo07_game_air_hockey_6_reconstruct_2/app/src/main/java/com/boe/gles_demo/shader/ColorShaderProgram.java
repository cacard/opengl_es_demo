package com.boe.gles_demo.shader;

import static android.opengl.GLES20.glGetAttribLocation;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.R;
import com.boe.gles_demo.helper.LogHelper;

public class ColorShaderProgram extends ShaderProgram {

    // 顶点属性
    // ------------
    private final int aPositionLocation;
    private final int aColorLocation;

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
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aColorLocation);
    }
}
