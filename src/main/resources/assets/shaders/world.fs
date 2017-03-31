#version 140
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

//layout(binding = 0) uniform sampler2D tex;
layout (location = 0) uniform sampler2D textures[16];
layout (location = 1) uniform vec4 diffuseColors[];

out vec4 color_out;
in vec4 color_vs;
in vec2 uv_vs;
in vec3 normal_vs;
flat in int matID_vs;

void main(void) {
	color_out = color_vs * texture(textures[matID_vs], uv_vs) * diffuseColors[matID_vs] * normal_vs.y;
}