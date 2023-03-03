package com.boe.demo4_glsurfaceview;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderHelper {

    static final String TAG = "ShaderHelper";

    public static int linkProgram(String vShaderCode, String fShaderCode) {
        Log.i(TAG, "======================= link program START:======================= ");
        int vShader = loadVertexShader(vShaderCode);
        int fShader = loadFragmentShader(fShaderCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);
        GLES20.glLinkProgram(program);
        String log = GLES20.glGetProgramInfoLog(program);
        Log.i(TAG, "link program log:\n" + log);
        // todo:add check error

        Log.i(TAG, "======================= link program END:======================= ");
        return program;
    }

    public static int linkProgram(Context context, int vShaderResId, int fShaderResId) {
        String vShaderCode = readRawTextFile(context, vShaderResId);
        String fShaderCode = readRawTextFile(context, fShaderResId);
        return linkProgram(vShaderCode, fShaderCode);
    }

    public static int loadVertexShader(String vShaderCode) {
        return loadShaderByShaderCode(GLES20.GL_VERTEX_SHADER, vShaderCode);
    }

    public static int loadFragmentShader(String fShaderCode) {
        return loadShaderByShaderCode(GLES20.GL_FRAGMENT_SHADER, fShaderCode);
    }

    /**
     * Load Shader
     *
     * @param type       GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode
     * @return
     */
    public static int loadShaderByShaderCode(int type, String shaderCode) {

        Log.i(TAG, "======================= compile shader START:======================= ");

        Log.i(TAG, "------ source shader code START ------");
        Log.i(TAG, shaderCode);
        Log.i(TAG, "------ source shader code END ------");

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);


        // check status
        // ----------------------------
        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            String log = GLES20.glGetShaderInfoLog(shader);
            Log.i(TAG, "------ compile log START ------");
            Log.i(TAG, log);
            Log.i(TAG, "------ compile log END ------");
            throw new RuntimeException("Error creating shader.");
        }

        Log.i(TAG, "======================= compile shader END:======================= ");
        return shader;
    }

    /**
     * Load shader by resId
     *
     * @param context
     * @param type
     * @param resId
     * @return
     */
    public static int loadShaderByResId(Context context, int type, int resId) {
        String code = readRawTextFile(context, resId);
        return loadShaderByShaderCode(type, code);
    }

    /**
     * load vertex shader by resId
     *
     * @param context
     * @param resId
     * @return
     */
    public static int loadVertexShaderByResId(Context context, int resId) {
        return loadShaderByResId(context, GLES20.GL_VERTEX_SHADER, resId);
    }

    /**
     * load fragment shader by resId
     *
     * @param context
     * @param resId
     * @return
     */
    public static int loadFragmentShaderByResId(Context context, int resId) {
        return loadShaderByResId(context, GLES20.GL_FRAGMENT_SHADER, resId);
    }

    private static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkGLError(String label) {
        checkGLError(label, true);
    }

    public static void checkGLError(String label, boolean errorThrow) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            if (errorThrow) {
                throw new RuntimeException(label + ": glError " + error);
            }
        }
    }

}
