#version 140
#extension GL_ARB_shading_language_420pack : enable

out vec4 color_out;
in vec4 color_vs;

void main(void) {
	color_out = color_vs;
}