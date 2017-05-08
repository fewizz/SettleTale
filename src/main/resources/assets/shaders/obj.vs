#version 140

#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_shading_language_420pack : enable

layout (location = 0) in vec4 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uv;
layout (location = 3) in int flags;

out vec3 normal_vs;
out vec2 uv_vs;
flat out int hasUV;
flat out int hasNormal;
flat out int matID;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
};

void main(void) {
	normal_vs = normal;
	uv_vs = uv;
	//flags_vs = flags;
	
	matID = 0;//flags & 0xFF;
	hasUV = 0;//(flags >> 8) & 0x1;
	hasNormal = 0;//(flags >> 9) & 0x1;
	
	gl_Position = projMat * viewMat * pos;
}