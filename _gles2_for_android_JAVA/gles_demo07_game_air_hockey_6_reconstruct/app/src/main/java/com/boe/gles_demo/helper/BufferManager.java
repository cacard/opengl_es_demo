package com.boe.gles_demo.helper;

import com.boe.gles_demo.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferManager {


    static  float[] table = {
            // ---X,Y,Z--, --R,G,B---, --U,V--
            // !!! 这个例子中，RGB已被Texture取代，所以RGB是不需要的，临时放这里也行
            // Triangle Fan——即以扇形旋转的方式绘制三角形
            0f, 0f, 0f, 1f, 1f, 1f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,
            0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.9f,
            0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.7f, 0.7f, 0.7f, 0f, 0.9f,

    };

    static  float[] divider = {
            // ---X,Y,Z--
            // 中线 ——虽然不需要UV，但需要补齐
            -0.5f, 0f, 0.05f,
            0.5f, 0f, 0.05f
    };

    static FloatBuffer vertexData = null;

    public static FloatBuffer getBuffer() {
        return vertexData;
    }

    public static void init() {

        List<float[]> vercitesList = new ArrayList<>();
        vercitesList.add(table);
        vercitesList.add(divider);

        int len = 0;
        for (float[] f : vercitesList) {
            len += f.length;
        }
        vertexData = ByteBuffer.allocateDirect(len * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (float[] f : vercitesList) {
            vertexData.put(f);
        }
        vertexData.position(0);
    }

    public static void positionTable() {
        vertexData.position(0);
    }

    public static void positionDivider() {
        vertexData.position(table.length);
    }

}
