package com.boe.gles_demo.objects;

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

        public Point translateY(float dis) {
            return new Point(x, y + dis, z);
        }

        // 按向量位移
        public Point translate(Vector vector) {
            return new Point(x + vector.x,
                    y + vector.y,
                    z + vector.z);
        }
    }

    /**
     * 圆形
     */
    public static class Circle {
        public final Point center;
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
     * <p>
     * - 起始顶点（Point）
     * - 向量
     */
    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    /**
     * 向量
     * <p>
     * 这个向量仅包含 x/y/z
     * 起始点是原点
     * 向量长度是 平方和开根号
     */
    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * 叉乘
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
         * 向量长度 = 平方和，开根号
         */
        public float length() {
            return (float) Math.sqrt(
                    x * x + y * y + z * z);
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    /**
     * 球体
     */
    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    /**
     * Ray 到某个 Point 的距离
     *
     * @param point 点
     * @param ray   线段
     * @return
     */
    public static float distanceBetween(Point point, Ray ray) {
        // Ray的一头的顶点到point形成的向量
        Vector p1ToPoint = vectorBetween(ray.point, point);
        // Ray的另一头的顶点
        Point p2 = ray.point.translate(ray.vector);
        // Ray的另一头的顶点到point形成的向量
        Vector p2ToPoint = vectorBetween(p2, point);

        // 三个点（Ray两个点、point）组成的三角形的面积的2倍
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        // Ray向量长度
        float lengthOfBase = ray.vector.length();
        // 根据面积计算法则，可知道point垂直于Ray的高度
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }


}
