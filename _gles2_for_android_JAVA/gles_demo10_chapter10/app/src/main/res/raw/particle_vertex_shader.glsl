
uniform mat4 u_Matrix; //投影矩阵
uniform float u_Time; //当前时间

attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_DirectionVector;
attribute float a_ParticleStartTime; //开始时间

// 输出到Fragment
// ----------
varying vec3 v_Color; //输出：颜色值
varying float v_ElapsedTime; //输出：已过去的时长

void main () {
    v_Color = a_Color;

    // 随着时间的推移，位置发生改变
    v_ElapsedTime = u_Time - a_ParticleStartTime;
    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
    gl_Position = u_Matrix * vec4(currentPosition, 1.0);

    gl_Position = 10.0
}

