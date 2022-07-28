package com.boe.demo3_texture_view;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageHelper {

    static Bitmap convert(Bitmap bitmapSrc) {


        Matrix matrix = getMatrix(bitmapSrc);
        // check to make sure your mapping succeeded
        // if your source polygon is a distorted rectangle, you should be okay
        if (matrix != null) {
            // create a new bitmap from the original bitmap using the matrix for transform
            Bitmap imageOut = Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
            return imageOut;
        }

        return null;
    }

    static Matrix getMatrix(Bitmap bitmapSrc) {
        // Set up a source polygon.
        // X and Y values are "flattened" into the array.
        float[] src = new float[8];
        // top left x,y
        src[0] = 0;   // from your diagram
        src[1] = 0;
        // top right x,y
        src[2] = bitmapSrc.getWidth();
        src[3] = 0;
        // bottom right x,y
        src[4] = bitmapSrc.getWidth();
        src[5] = bitmapSrc.getHeight();
        // bottom left x,y
        src[6] = 0;
        src[7] = bitmapSrc.getHeight();

        // set up a dest polygon which is just a rectangle
        float[] dst = new float[8];
        dst[0] = bitmapSrc.getWidth() * 0.3f;
        dst[1] = bitmapSrc.getHeight() * 0.1f;
        dst[2] = bitmapSrc.getWidth();
        dst[3] = 0;
        dst[4] = bitmapSrc.getWidth() * 0.8f;
        dst[5] = bitmapSrc.getHeight() * 0.9f;
        dst[6] = bitmapSrc.getWidth() * 0.5f;
        dst[7] = bitmapSrc.getHeight() * 0.9f;

        // create a matrix for transformation.
        Matrix matrix = new Matrix();

        // set the matrix to map the source values to the dest values.
        boolean mapped = matrix.setPolyToPoly(src, 0, dst, 0, 4);
        if (!mapped) {
            return null;
        }
        return matrix;
    }

    public static boolean changeMatrix(int w, int h, Matrix matrix) {
        // Set up a source polygon.
        // X and Y values are "flattened" into the array.
        float[] src = new float[8];
        // top left x,y
        src[0] = 0;   // from your diagram
        src[1] = 0;
        // top right x,y
        src[2] = w;
        src[3] = 0;
        // bottom right x,y
        src[4] = w;
        src[5] = h;
        // bottom left x,y
        src[6] = 0;
        src[7] = h;

        // set up a dest polygon which is just a rectangle
        float[] dst = new float[8];
        dst[0] = w * 0.3f;
        dst[1] = w * 0.1f;
        dst[2] = w;
        dst[3] = 0;
        dst[4] = w * 0.8f;
        dst[5] = h * 0.9f;
        dst[6] = w * 0.5f;
        dst[7] = h * 0.9f;


        return matrix.setPolyToPoly(src, 0, dst, 0, 4);
    }


    public static boolean changeMatrix(int w, int h, Matrix matrix, float f) {
        // Set up a source polygon.
        // X and Y values are "flattened" into the array.
        float[] src = new float[8];
        // top left x,y
        src[0] = 0;   // from your diagram
        src[1] = 0;
        // top right x,y
        src[2] = w;
        src[3] = 0;
        // bottom right x,y
        src[4] = w;
        src[5] = h;
        // bottom left x,y
        src[6] = 0;
        src[7] = h;

        // set up a dest polygon which is just a rectangle
        float[] dst = new float[8];

        dst[0] = w * 0.1f + (w * 0.1f * f);
        dst[1] = w * 0.1f + (h * 0.33f * f);

        dst[2] = w * (1-f);
        dst[3] = h * f;

        dst[4] = w * 0.8f - (w * f * 0.3f);
        dst[5] = h * 0.9f- (h * f * 0.8f);

        dst[6] = w * 0.6f - (w * f);
        dst[7] = h * 0.9f * (1-f);


        return matrix.setPolyToPoly(src, 0, dst, 0, 4);
    }
}
