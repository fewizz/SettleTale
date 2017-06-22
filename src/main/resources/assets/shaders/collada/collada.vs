#version 140

#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

layout (location = 0) in vec4 pos;
layout (location = 1) in vec3 normal;
//layout (location = 2) in vec2 uv;
layout (location = 3) in int flags;

out vec3 normal_vs;
//out vec2 uv_vs;
flat out int flags_vs;

#include shaders/lib/uniformTransformCombined.glsl

layout (location = 0) uniform mat4 modelMat;

void main(void) {
	normal_vs = normal;
	//uv_vs = uv;
	flags_vs = flags;
	
	gl_Position = combinedMat * pos;
}