package com.boe.gles_demo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.boe.gles_demo.helper.CameraHelper;
import com.boe.gles_demo.helper.LogHelper;
import com.boe.gles_demo.helper.TextureHelper;
import com.boe.gles_demo.objects.Geometry;
import com.boe.gles_demo.shader.ColorShaderProgram;
import com.boe.gles_demo.shader.TextureShaderProgram;
import com.boe.gles_demo.shape.Mallet2;
import com.boe.gles_demo.shape.Puck;
import com.boe.gles_demo.shape.ShapeRay;
import com.boe.gles_demo.shape.Table;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    Context context;
    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 600;

    Table table;
    Mallet2 mallet;
    Puck puck;

    TextureShaderProgram textureShaderProgram;
    ColorShaderProgram colorShaderProgram;
    int textureId;

    float angleDefault = -0f;
    float angle = angleDefault;
    long lastTick = System.currentTimeMillis();
    boolean enableAnim = false;

    // 是否点中了Mallet，根据触摸点生成的Ray与代表Mallet的球体是否有交集判定
    boolean malletPressed = false;
    // Mallet在3D空间中的位置，即：通过用户触摸点更新Mallet的位置
    Geometry.Point blueMalletPosition;


    // 所有物体都具有的基础MVP
    float[] projectionMatrix = new float[16];
    // 在基础MVP的基础上对Model做二次变换时的临时变量
    float[] tempModelMatrix = new float[16];
    // 基础MVP的逆矩阵。【逆矩阵就是：最初的矩阵做了变换之后，如果再乘以这个逆矩阵就变成了最初的矩阵】
    private final float[] invertedViewProjectionMatrix = new float[16];


    public MyRenderer(Context context) {
        this.context = context;
    }

    public void toggleAnim() {
        enableAnim = !enableAnim;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试
        GLES20.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogHelper.log("->onSurfaceChanged()");
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        GLES20.glViewport(0, 0, width, height);

        // 创建各个物理对象、program、加载纹理
        // ------------
        table = new Table();
        mallet = new Mallet2(0.1f, 0.3f, 55);
        puck = new Puck(0.1f, 0.06f, 55);
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        textureId = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

        // 初始化蓝色Mallet的位置，在桌板下侧中间位置
        blueMalletPosition = new Geometry.Point(0f, -0.4f, (mallet.height / 2f + 0.001f));
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        //LogHelper.log("->onDrawFrame()");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        // 按时间旋转
        if (enableAnim && System.currentTimeMillis() - lastTick > 14) {
            angle -= 0.2;
            lastTick = System.currentTimeMillis();
            if (angle < -120) {
                angle = angleDefault;
                enableAnim = false;
            }
        }

        // 透视投影变换：全局
        projectionMatrix = CameraHelper.getMVP(SCREEN_WIDTH, SCREEN_HEIGHT, angle);
        // 得到逆矩阵，目的是将2D NDC转场 3D空间坐标
        Matrix.invertM(invertedViewProjectionMatrix, 0, projectionMatrix, 0);

        // 绘制Table
        // ------------
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniformTexture(textureId);
        textureShaderProgram.setUniformMatrix(projectionMatrix);
        table.bindData(textureShaderProgram);
        table.draw();
        textureShaderProgram.release();


        // 绘制Mallet：两个
        // ------------
        // Mallet蓝色
        float[] mallet1MVP = projectionMatrix.clone();
        Matrix.setIdentityM(tempModelMatrix, 0);
        //按照blueMalletPosition设置的位置来在基础MVP的基础上再偏移
        Matrix.translateM(tempModelMatrix, 0, blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        // 矫正模型角度【注意顺序】
        Matrix.rotateM(tempModelMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.multiplyMM(mallet1MVP, 0, mallet1MVP, 0, tempModelMatrix, 0);

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniformMatrix(mallet1MVP);
        colorShaderProgram.setUniformColor(0, 0, 1);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        // Mallet绿色
        float[] mallet2MVP = projectionMatrix.clone();
        Matrix.setIdentityM(tempModelMatrix, 0);
        // 旋转Mallet到正常角度
        Matrix.rotateM(tempModelMatrix, 0, 90, 1f, 0f, 0f);
        // 调整Mallet的位置到初始位置
        Matrix.translateM(tempModelMatrix, 0, 0, mallet.height / 2f, -0.5f);
        // 根据触摸位置，继续调整Mallet的位置

        Matrix.multiplyMM(mallet2MVP, 0, mallet2MVP, 0, tempModelMatrix, 0);

        colorShaderProgram.setUniformMatrix(mallet2MVP);
        colorShaderProgram.setUniformColor(0, 1, 0);
        mallet.draw();


        // 绘制冰球
        // ------------

        // MVP矩阵：设置初始位置
        float[] projectionMatrixPuck = projectionMatrix.clone();
        tempModelMatrix = new float[16];
        Matrix.setIdentityM(tempModelMatrix, 0);
        Matrix.rotateM(tempModelMatrix, 0, 90, 1f, 0f, 0f);
        Matrix.translateM(tempModelMatrix, 0, 0f, 0.03f, (float) Math.sin(angle / 10) / 4f);
        Matrix.multiplyMM(projectionMatrixPuck, 0, projectionMatrixPuck, 0, tempModelMatrix, 0);

        colorShaderProgram.setUniformMatrix(projectionMatrixPuck);
        colorShaderProgram.setUniformColor(1, 1, 0);
        puck.bindData(colorShaderProgram);
        puck.draw();

        // 绘制Ray
        // ----------
        if (shapeRay != null) {
            float[] rayMatrix = new float[16];
            Matrix.setIdentityM(rayMatrix, 0);
            tempModelMatrix = new float[16];
            Matrix.setIdentityM(tempModelMatrix, 0);
            //Matrix.rotateM(tempModelMatrix, 0, 0, 0f, 0f, 1f);
            //Matrix.translateM(tempModelMatrix, 0, 0f, 0.5f, 0);
            //Matrix.multiplyMM(rayMatrix, 0, rayMatrix, 0, tempModelMatrix, 0);
            colorShaderProgram.setUniformMatrix(rayMatrix);
            colorShaderProgram.setUniformColor(1, 0, 0);
            shapeRay.bindData(colorShaderProgram);
            shapeRay.draw();
        }

        colorShaderProgram.release();


    }

    Geometry.Ray rayOnPress;
    ShapeRay shapeRay;

    Geometry.Sphere sphereOnPress;

    /**
     * 鼠标按下时
     */
    public void handleTouchPress(float x, float y) {
        LogHelper.log(String.format("【@MyRenderer】handleTouchPress() x:%s,y:%s", x, y));

        // 将屏幕2D转化为3D空间中的射线（线段）
        Geometry.Ray ray = convertNormalized2DPointToRay(x, y);
        rayOnPress = ray;
        shapeRay = new ShapeRay(rayOnPress);
        //enableAnim = true;

        // 根据蓝色Mallet在3D空间中的位置，生成一个球体
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(
                new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z),
                mallet.height / 2f);
        sphereOnPress = malletBoundingSphere;

        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
        LogHelper.log(String.format("【@MyRenderer】handleTouchPress() malletPressed:%s", malletPressed));

        // 创建一个渲染的Ray图形
        shapeRay = new ShapeRay(ray);
    }

    public void handleTouchDrag(float x, float y) {
        LogHelper.log(String.format("【@MyRenderer】handleTouchDrag() x:%s,y:%s/malletPressed:%s", x, y, malletPressed));
        if (!malletPressed) {
            return;
        }
        Geometry.Ray ray = convertNormalized2DPointToRay(x, y);
        // 生成一个平面，代表桌子，而桌子的位置就是在原点，方向是朝向Z正前方
        Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0),
                new Geometry.Vector(0, 0, 0.01f));
        Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
        // 根据触摸点更新BlueMallet的位置，【Z值的特殊处理：因为触摸点是触摸的桌面，所以Mallet要高出桌面】
        blueMalletPosition = new Geometry.Point(touchedPoint.x, touchedPoint.y, touchedPoint.z + (mallet.height / 2f + 0.001f));
        LogHelper.log(String.format("【@MyRenderer】handleTouchDrag() blueMalletPosition:%s", blueMalletPosition.toString()));

    }

    // 将2D平面中已被标准化的坐标转化为3D世界中的射线
    private Geometry.Ray convertNormalized2DPointToRay(float x, float y) {

        // 触摸位置的 near 位置，z=-1
        final float[] nearPointNdc = {x, y, -1, 1};
        // 触摸位置的 far 位置，z=1
        final float[] farPointNdc = {x, y, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        // 投影透视中，将三维坐标通过MVP变换成2D NDC坐标
        // 反过来，将2D NDC 乘以 （MVP的逆） = 三维坐标空间中的点
        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        // 为什么要divide？
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPoint =
                new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPoint =
                new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Geometry.Ray(nearPoint, Geometry.vectorBetween(nearPoint, farPoint));

    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

}
