#version 140
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

in vec3 normal_vs;
in vec2 uv_vs;
flat in int matID_vs;
out vec4 color_out;

layout (location = 0) uniform sampler2D textures[256];

void main(void) {
	color_out = texture(textures[matID_vs], uv_vs);
	color_out = vec4(color_out.xyz * normal_vs.y, color_out.w);
}