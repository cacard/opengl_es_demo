precision mediump float;

uniform sampler2D u_TextureUnit; //输入的纹理
varying vec2 v_TextureCoordinates; //从vertex shader中接收到的纹理坐标

void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates); //颜色改用纹理
}