#version 140

#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_shading_language_420pack : enable

layout (location = 0) in vec4 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uv;
layout (location = 3) in int matID;

out vec3 normal_vs;
out vec2 uv_vs;
flat out int matID_vs;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
};

void main(void) {
	normal_vs = normal;
	uv_vs = uv;
	matID_vs = matID;
	gl_Position = projMat * viewMat * pos;
}