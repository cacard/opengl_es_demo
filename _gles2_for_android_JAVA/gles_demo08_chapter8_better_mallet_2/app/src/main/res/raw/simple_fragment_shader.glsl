precision mediump float;

varying vec4 v_Color; //从vertex shader中接收颜色值
uniform vec4 u_Color; //从unifrom变量支持传入颜色值

void main()
{
    gl_FragColor = u_Color;
}