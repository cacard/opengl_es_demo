package com.boe.gles_demo.shader;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;

import android.content.Context;
import android.opengl.GLES20;

import com.boe.gles_demo.R;
import com.boe.gles_demo.helper.LogHelper;

public class TextureShaderProgram extends ShaderProgram {

    // uniform location
    // ------------

    final int uTextureUnitLocation;

    // attrib location
    // 设置值的方式可以是：向外暴露，由外部设置
    // ------------
    final int aPositionLocation;
    final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        // get locations
        // ------------
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        if (uTextureUnitLocation < 0) {
            LogHelper.logE("【@TextureShaderProgram】uTextureUnitLocation=" + uTextureUnitLocation);
        }

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        if (aPositionLocation < 0) {
            LogHelper.logE("【@TextureShaderProgram】aPositionLocation=" + aPositionLocation);
        }
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        if (aTextureCoordinatesLocation < 0) {
            LogHelper.logE("【@TextureShaderProgram】aTextureCoordinatesLocation=" + aTextureCoordinatesLocation);
        }
    }

    /**
     * 设置texture
     *
     * @param textureId 加载的texture id
     */
    public void setUniformTexture(int textureId) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesLocation() {
        return aTextureCoordinatesLocation;
    }

    @Override
    public void release() {
        super.release();
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordinatesLocation);
    }
}
