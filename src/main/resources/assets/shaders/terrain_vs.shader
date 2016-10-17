#version 420 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 color;

layout (binding = 0, std140) uniform Transform
{
	mat4 projMat;
	mat4 viewMat;
} transf;

out vec4 color_vs;

void main(void) {
	color_vs = vec4(color.xyz, 0);
	gl_Position = transf.projMat * transf.viewMat * vec4(pos.xyz, 1);
}