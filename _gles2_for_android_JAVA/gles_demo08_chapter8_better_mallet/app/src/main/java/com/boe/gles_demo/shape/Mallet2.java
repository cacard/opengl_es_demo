package com.boe.gles_demo.shape;

import com.boe.gles_demo.data.VertexArray;
import com.boe.gles_demo.objects.Geometry;
import com.boe.gles_demo.objects.ObjectBuilder;
import com.boe.gles_demo.shader.ColorShaderProgram;

import java.util.List;

public class Mallet2 {

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Mallet2(float radius, float height, int numPoints) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(
                new Geometry.Point(0f, 0f, 0f),
                radius,
                height,
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
