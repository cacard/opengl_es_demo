precision mediump float;
uniform vec4 u_Color;

uniform sampler2D u_Texture; //输入的纹理

varying vec4 v_Color; //从vertex shader中接收颜色值
varying vec2 v_UV; //从vertex shader中接收到的纹理坐标

void main()
{
    gl_FragColor = texture2D(u_Texture, v_UV); //颜色改用纹理
}