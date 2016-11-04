#version 420 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 color;

out vec4 color_vs;

layout (binding = 2, std140) uniform Info
{
	vec3[] ;
	float h;
} info;

void main(void) {
	color_vs = vec4(color.xyz, 0);
	gl_Position = vec4(pos.xyz, 1);
}