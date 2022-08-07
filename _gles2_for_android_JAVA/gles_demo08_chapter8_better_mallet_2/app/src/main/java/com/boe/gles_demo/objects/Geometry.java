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

}
