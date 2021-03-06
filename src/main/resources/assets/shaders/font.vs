#version 140

#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_shading_language_420pack : enable

layout (location = 0) in vec3 pos;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 uv;
layout (location = 3) in int texID;

out vec4 color_vs;
out vec2 uv_out;
flat out int texID_vs;

#include shaders/lib/uniformTransformCombined.glsl

void main(void) {
	color_vs = color;
	uv_out = uv;
	texID_vs = texID;
	gl_Position = combinedMat * vec4(pos, 1);
}