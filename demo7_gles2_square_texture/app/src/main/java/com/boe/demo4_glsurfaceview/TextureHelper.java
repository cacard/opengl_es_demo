
package com.boe.demo4_glsurfaceview;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId, boolean isTransparent) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Load the bitmap into the bound texture.
        if (isTransparent) {
            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
            bitmap.copyPixelsToBuffer(buffer);
            buffer.position(0);
            GLES20.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap.getWidth(), bitmap.getHeight(),
                    0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else {
            // 走的GLUtil，自动判定图片格式
            texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        }

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        glGenerateMipmap(GL_TEXTURE_2D);

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }


    /**
     * Loads a cubemap texture
     *
     * @param context
     * @param cubeResources order: left, right, bottom, top, front, back.
     * @return 0 failed
     */
    public static int loadCubeMap(Context context, int[] cubeResources) {

        // gen texture id
        // ----------------
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        ShaderHelper.checkGLError("TextureHelper#loadCubeMap()", false);
        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        } else {
            Log.i(TAG, "loadCubeMap() texture id:" + Arrays.toString(textureObjectIds));
        }


        // load bitmaps
        // --------------
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), cubeResources[i], options);
            if (cubeBitmaps[i] == null) {
                Log.w(TAG, "Resource ID " + cubeResources[i] + " could not be decoded.");
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }

        // Linear filtering for minification and magnification
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);

        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle();
        }

        return textureObjectIds[0];
    }

    /**
     * load many cubemap texture one time
     *
     * @param context
     * @param cubeResourcesList
     * @return
     */
    public static int[] loadCubeMaps(Context context, List<int[]> cubeResourcesList) {

        int count = cubeResourcesList.size();

        // gen texture id
        // ----------------
        final int[] textureObjectIds = new int[count];
        glGenTextures(count, textureObjectIds, 0);

        ShaderHelper.checkGLError("TextureHelper#loadCubeMap()", false);

        // check all
        for (int id : textureObjectIds) {
            if (id == 0) {
                Log.w(TAG, "Could not generate a new OpenGL texture object. ");
                return null;
            }
        }
        Log.i(TAG, "loadCubeMap() texture id:" + Arrays.toString(textureObjectIds));


        // load bitmaps
        // --------------
        for (int textureIndex = 0; textureIndex < textureObjectIds.length; textureIndex++) {
            int[] resources = cubeResourcesList.get(textureIndex);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap[] cubeBitmaps = new Bitmap[6];
            for (int i = 0; i < 6; i++) {
                cubeBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), resources[i], options);
                // check
                if (cubeBitmaps[i] == null) {
                    Log.w(TAG, "Resource ID " + resources[i] + " could not be decoded.");
                    glDeleteTextures(1, textureObjectIds, 0);
                    return null; // all fail
                }
            }

            // Linear filtering for minification and magnification
            glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[textureIndex]);

            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
            texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
            texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
            texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
            texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
            texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

            glBindTexture(GL_TEXTURE_2D, 0);

            for (Bitmap bitmap : cubeBitmaps) {
                bitmap.recycle();
            }
        }

        return textureObjectIds;
    }


}
