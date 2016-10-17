#version 420 core

out vec4 color_out;
in vec4 color_vs;

void main(void) {
	color_out = color_vs;
}