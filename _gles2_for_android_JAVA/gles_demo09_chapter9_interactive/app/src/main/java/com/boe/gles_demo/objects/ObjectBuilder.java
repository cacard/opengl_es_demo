package com.boe.gles_demo.objects;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

import android.opengl.GLES20;

import com.boe.gles_demo.Constants;

import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder {

    /**
     * 每个顶点单元包含几个float？
     *  - 3个（xyz）
     */
    public static final int FLOATS_PER_VERTEX = 3;

    /**
     * 顶点数据：是由外部传入的图形（圆、圆柱）对应的抽象数据而生成的！
     * 外部的圆、圆柱仅包含了简单的位置、大小、精细度信息，没有包含具体顶点数据！
     */
    final float[] vertexData;

    final List<DrawCommand> drawList = new ArrayList<>();

    private int offset = 0;

    /**
     * @param vertexCount 顶点数量：越多、越光滑
     */
    public ObjectBuilder(int vertexCount) {
        vertexData = new float[vertexCount * Constants.SIZE_OF_FLOAT];
    }

    /**
     * 为当前Object添加一个圆形。即：生成对应的顶点数据
     * 注意：圆形的绘制方式是TriangleFun，按这个中绘制方式生成顶点
     *
     * @param circle
     * @param numPoints 精细度，即：圆形切面数量
     */
    public void appendCircle(Geometry.Circle circle, int numPoints) {

        // 开始绘制的顶点（注意：每个顶点是3个float）index
        int drawStartVertexIndex = offset / FLOATS_PER_VERTEX;
        // 绘制的顶点数量
        int drawVertexCount = sizeOfCircleInVertices(numPoints);

        // 中心点
        // ------------
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // 扇面其它点位
        // !!! 注意，i 小于等于 表明是i+1个顶点，即：最后一个顶点其实就是第一个
        for (int i = 0; i <= numPoints; i++) {
            // 角度
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            // x
            vertexData[offset++] = (float) (circle.center.x + circle.radius * Math.cos(angleInRadians));
            // y
            vertexData[offset++] = circle.center.y;
            // z
            vertexData[offset++] = (float) (circle.center.z + circle.radius * Math.sin(angleInRadians));
        }

        // 添加到绘制列表（DrawList）
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, drawStartVertexIndex, drawVertexCount);
            }
        });
    }

    /**
     * 为当前Object添加一个圆柱体（侧面）。即：生成对应的顶点数据
     *
     * @param cylinder
     * @param numPoints 精细度：面数
     */
    public void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        // 开始绘制的顶点index
        final int startVertex = offset / FLOATS_PER_VERTEX;
        // 绘制的顶点数量
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        // y:开始值
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        // y:结束值
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        // 产生顶点数据
        // !!! 注意： i 小于等于 的意思是多一个顶点，即最后一组就是第一组
        for (int i = 0; i <= numPoints; i++) {
            // 角度
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            // x
            float x = (float) (cylinder.center.x + cylinder.radius * Math.cos(angleInRadians));
            // z
            float z = (float) (cylinder.center.z + cylinder.radius * Math.sin(angleInRadians));

            // 两个顶点
            // ------------
            vertexData[offset++] = x;
            vertexData[offset++] = yStart;
            vertexData[offset++] = z;
            vertexData[offset++] = x;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = z;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    public GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    // ------------------------------------ STATIC Methods ----------------------------------------
    // todo:以下方法可以看做是Helper，可以抽离出来

    /**
     * 冰球：一个圆柱体侧面+顶部圆
     *
     * @param puck
     * @param numPoints
     * @return
     */
    public static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        // 计算当前Puck一共需要多少顶点：
        // 圆柱体侧面+顶部圆形的所有顶点数量
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);

        // 创建上面的圆形
        // ------------
        Geometry.Point circleCenterPoint = puck.center.translateY(puck.height / 2f);
        Geometry.Circle circle = new Geometry.Circle(circleCenterPoint, puck.radius);

        builder.appendCircle(circle, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    /**
     * 球棍：两个Puck
     *
     * @param center
     * @param radius
     * @param height
     * @param numPoints
     * @return
     */
    public static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints) {
        // 顶点数量（注意是需要两个圆柱体）
        int size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;

        ObjectBuilder builder = new ObjectBuilder(size);

        // 底部：基座圆柱体高度
        float baseHeight = height * 0.25f;

        Geometry.Circle baseCircle = new Geometry.Circle(center.translateY(-baseHeight), radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        // 上部：手柄
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        Geometry.Circle handleCircle = new Geometry.Circle(
                center.translateY(height * 0.5f),
                handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

    /**
     * 圆形（柱体顶部）的顶点数量
     * 使用TriangleFan绘制，圆形中心点算一个顶点，圆形边缘首位有一个重复的顶点
     *
     * @param numPoints 圆柱体平面数？
     * @return
     */
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    /**
     * 圆柱体（所有面）的顶点数量
     * 使用TriangleStrip绘制
     *
     * @param numPoints
     * @return
     */
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    public static interface DrawCommand {
        void draw();
    }

    /**
     * 生成的数据
     * - 顶点数据
     * - 绘制数据
     */
    public static class GeneratedData {
        public final float[] vertexData;
        public final List<DrawCommand> drawList;

        public GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }
}
