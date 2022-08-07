package com.boe.gles_demo.shape;

import com.boe.gles_demo.data.VertexArray;
import com.boe.gles_demo.objects.Geometry;
import com.boe.gles_demo.objects.ObjectBuilder;
import com.boe.gles_demo.shader.ColorShaderProgram;

import java.util.List;

/**
 * 冰球
 */
public class Puck {

    public final float radius, height;

    // 顶点数据
    private final VertexArray vertexArray;

    // 绘制数据DrawList
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPoints) {

        this.radius = radius;
        this.height = height;

        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0, 0, 0), radius, height),
                numPoints);
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram program) {
        // 顶点属性:Position
        int positionLocation = program.getPositionLocation();
        vertexArray.setVertexAttribPointer(0, positionLocation, 3, 0);
    }

    public void draw() {
        vertexArray.resetPosition();
        for (ObjectBuilder.DrawCommand command : drawList) {
            command.draw();
        }
    }
}
