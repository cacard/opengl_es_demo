package com.boe.gles_demo.objects;

import androidx.annotation.NonNull;

/**
 * 几何体抽象
 * <p>
 * - 几何体仅仅是用最简化的信息来表示一个几何形状
 * - 一个圆柱体可以用中心点、高度、半径、面精细度表示
 * - 等
 */
public class Geometry {

    /**
     * 3D空间中的点
     */
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        // 按Y方向位移
        public Point translateY(float dis) {
            return new Point(x, y + dis, z);
        }

        // 按向量位移
        public Point translate(Vector vector) {
            return new Point(x + vector.x,
                    y + vector.y,
                    z + vector.z);
        }

        @NonNull
        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    /**
     * 圆
     */
    public static class Circle {

        // 圆的中心点
        public final Point center;

        // 圆的半径
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    /**
     * 圆柱体
     */
    public static class Cylinder {

        /**
         * 圆柱体中心点
         */
        public final Point center;

        /**
         * 半径
         */
        public final float radius;

        /**
         * 高度
         */
        public final float height;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    /**
     * 射线 （或者叫线段）
     */
    public static class Ray {

        /**
         * 起始点
         */
        public final Point point;

        /**
         * 向量（代表长度和方向）
         */
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    /**
     * 向量
     * 这个向量仅包含 x/y/z，起始点是原点
     */
    public static class Vector {

        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * 叉乘 Cross Product
         * <p>
         * 两个向量（不平行）的叉乘的结果是一个新向量，
         * 新向量方向：垂直于两者，方向按右手坐标系定。
         * 新向量长度：是两个向量组成的矩形/菱形的面积。
         */
        public Vector crossProduct(Vector other) {
            return new Vector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        /**
         * 点乘 Dot Product
         * 两个向量的点乘结果是一个数，等于： A向量在B向量上的投影 x B向量的长度
         */
        public float dotProduct(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        /**
         * 向量长度 = 平方和，开根号
         */
        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        /**
         * 缩放
         */
        public Vector scale(float scale) {
            return new Vector(x * scale, y * scale, z * scale);
        }
    }


    /**
     * 球体
     */
    public static class Sphere {

        // 球体中心点
        public final Point center;

        // 球体半径
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    /**
     * 平面
     * 一个点、一个向量，决定一个平面
     */
    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    // ------------------------------------ Helper --------------------------------

    /**
     * 两个Point之间形成的Vector
     */
    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    /**
     * Ray 到某个 Point 的距离
     * 原理是：Ray的两个点与Point连接，Point垂直于Ray的直线就是距离
     * 根据面积公式可以计算
     */
    public static float distanceBetween(Point point, Ray ray) {
        // Ray的一头的顶点到point形成的向量
        Vector vectorP1ToPoint = vectorBetween(ray.point, point);
        // Ray的另一头的顶点
        Point p2 = ray.point.translate(ray.vector);
        // Ray的另一头的顶点到point形成的向量
        Vector vectorP2ToPoint = vectorBetween(p2, point);

        // 三个点（Ray两个点、point）组成的三角形的面积的2倍
        // 这里使用了叉乘：两个向量的叉乘就是组成的三角形面积的2倍
        float areaOfTriangleTimesTwo = vectorP1ToPoint.crossProduct(vectorP2ToPoint).length();
        // Ray向量长度
        float lengthOfBase = ray.vector.length();
        // 根据面积计算法则，可知道point垂直于Ray的高度
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    /**
     * 相交判定：一个Sphere球体与一个Ray射线是否橡胶
     * 这个比较容易：做辅助线、叉乘、面积、直角三角形原理等
     */
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    /**
     * 相交判定：一个射线与一个平面是否相交
     * 这个相对比较复杂：TODO:需要具体理解
     */
    public static Point intersectionPoint(Ray ray, Plane plane) {
        // 射线与平面法线点的连线的向量
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        // 缩放系数
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                / ray.vector.dotProduct(plane.normal);
        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }


}
