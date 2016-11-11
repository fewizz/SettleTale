#version 420 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 normal;

out vec4 color_vs;
out vec3 normal_vs;
out float y_vs;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
} transf;

void main(void) {
	color_vs = vec4(color.xyz, 0);
	normal_vs = normal;
	y_vs = pos.y;
	gl_Position = transf.projMat * transf.viewMat * vec4(pos, 1);
}