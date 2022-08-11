package com.boe.gles_demo.shape;

import com.boe.gles_demo.data.VertexArray;
import com.boe.gles_demo.objects.DrawCommand;
import com.boe.gles_demo.objects.Geometry;
import com.boe.gles_demo.objects.ObjectBuilder;
import com.boe.gles_demo.shader.ColorShaderProgram;

import java.util.List;

public class ShapeRay {

    Geometry.Ray ray;

    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public ShapeRay(Geometry.Ray ray) {
        this.ray = ray;
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createRay(ray);
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
        for (DrawCommand command : drawList) {
            command.draw();
        }
    }
}
