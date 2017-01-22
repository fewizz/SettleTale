#version 140
#extension GL_ARB_shading_language_420pack : enable

in vec3 normal_vs;
out vec4 color_out;

void main(void) {
	color_out = vec4(vec3(1, 1, 1) * normal_vs.y, 1);// * vec4(normal_vs.xyz, 1);
}