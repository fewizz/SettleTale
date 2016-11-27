#version 430

#extension GL_ARB_explicit_attrib_location : enable
#extension GL_ARB_shading_language_420pack: enable

layout (location = 0) in vec3 pos;
layout (location = 1) in float id;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec4 ids1;
layout (location = 4) in vec4 ids2;


flat out float id_vs;
out vec3 normal_vs;
out vec3 pos_vs;

flat out vec4 ids1_vs;
flat out vec4 ids2_vs;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
} transf;

void main(void) {
	id_vs = id;
	normal_vs = normal;
	pos_vs = pos;
	ids1_vs = ids1;
	ids2_vs = ids2;
	gl_Position = transf.projMat * transf.viewMat * vec4(pos, 1);
}