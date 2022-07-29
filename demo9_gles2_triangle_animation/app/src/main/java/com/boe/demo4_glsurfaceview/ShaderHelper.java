package com.boe.demo4_glsurfaceview;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {

    // 编译连接vshader/fshader成program
    static int linkProgram(String vShaderCode, String fShaderCode) {
        int vShader = loadVertexShader(vShaderCode);
        int fShader = loadFragmentShader(fShaderCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);
        GLES20.glLinkProgram(program);
        String log = GLES20.glGetProgramInfoLog(program);
        Log.i("_MainActivity_", "link program log:" + log);
        return program;
    }

    static int loadVertexShader(String vShaderCode) {
        return loadShader(GLES20.GL_VERTEX_SHADER, vShaderCode);
    }

    static int loadFragmentShader(String fShaderCode) {
        return loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderCode);
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        String log = GLES20.glGetShaderInfoLog(shader);
        Log.i("_MainActivity_", "compile shader log:" + log);

        return shader;
    }

}
