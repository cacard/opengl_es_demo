attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_UV; //纹理坐标

uniform mat4 u_Matrix;

varying vec4 v_Color; //输出
varying vec2 v_UV; //输出：纹理坐标

void main()
{
    v_Color = a_Color;
    v_UV = a_UV;
    gl_Position =  u_Matrix * a_Position;
    gl_PointSize = 10.0;
}