#version 140
#extension GL_ARB_shading_language_420pack : enable

layout(binding = 0) uniform sampler2D tex;

out vec4 color_out;
in vec4 color_vs;
in vec2 uv_out;

void main(void) {
	color_out = color_vs * texture(tex, uv_out);
}