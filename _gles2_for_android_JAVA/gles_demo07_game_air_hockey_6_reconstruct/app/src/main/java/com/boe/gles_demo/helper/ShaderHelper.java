package com.boe.gles_demo.helper;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {

    // 编译连接vshader/fshader成program
    public static int linkProgram(String vShaderCode, String fShaderCode) {
        int vShader = loadVertexShader(vShaderCode);
        int fShader = loadFragmentShader(fShaderCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);
        GLES20.glLinkProgram(program);

        LogHelper.log("------------------ program link result ------------------");
        String log = GLES20.glGetProgramInfoLog(program);
        Log.i("_MainActivity_", "programId:" + program + " link program log:" + log);
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

        LogHelper.log("------------------ shader compile result ------------------");
        // Status为0时表示有错误
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        LogHelper.log("glGetShaderiv() status:" + String.valueOf(status[0]));

        String log = GLES20.glGetShaderInfoLog(shader);
        Log.i("_MainActivity_", "shader id:" + String.valueOf(shader) + " compile shader log:" + log);

        return shader;
    }

}
