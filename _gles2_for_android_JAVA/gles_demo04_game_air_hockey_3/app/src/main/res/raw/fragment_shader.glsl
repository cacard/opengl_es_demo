precision mediump float;
uniform vec4 u_Color;
varying vec4 v_Color; //从vertex shader中接收颜色值
void main()
{
    gl_FragColor = v_Color;
}