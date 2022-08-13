uniform mat4 u_Matrix; //MVP矩阵

attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates; //法线

varying vec2 v_TextureCoordinates; //输出法线

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_Matrix * a_Position;
}
