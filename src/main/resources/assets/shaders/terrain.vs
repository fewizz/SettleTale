#version 140

#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_shading_language_420pack : enable

layout (location = 0) in vec3 pos;
layout (location = 1) in float normal;

out float normal_vs;
out vec3 pos_vs;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
};

void main(void) {
	normal_vs = normal;
	pos_vs = pos;
	gl_Position = projMat * viewMat * vec4(pos, 1);
}