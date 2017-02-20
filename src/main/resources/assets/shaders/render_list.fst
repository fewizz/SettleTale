#version 330
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

@Cin vec4 color_vs;
@Nin vec3 normal_vs;
@Tin vec2 uv_vs;

@Tflat in int hasUV;
@Nflat in int hasNormal;
@Cflat in int hasColor;
@Mflat in int matID;

out vec4 color_out;

void main(void) {
	color_out = color_vs;

	@C /*
	color_out = vec4(1., 1., 1., 1.);
	@C */
}