#version 140
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

layout (location = 0) uniform sampler2D textures[16];

out vec4 color_out;
in vec4 color_vs;
in vec2 uv_out;
flat in int texID_vs;

float iqnoise(vec2 pos);

void main(void) {
	vec2 uv = uv_out * textureSize(textures[texID_vs], 0);
	color_out = color_vs;
	color_out.a *= smoothstep(0.0, 1., iqnoise(uv));
}

float iqnoise(vec2 pos) {
	ivec2 cell = ivec2(floor(pos));
	vec2 cellOffset = fract(pos);
	
	float value = 0.;
	float accum = 0.;
	
	for(int ix = -1; ix <= 1; ix++)
	for(int iy = -1; iy <= 1; iy++)
	{
		vec2 samplePos = vec2(float(ix), float(iy));
		vec2 locPos = samplePos - cellOffset;

		float centerDistance = length(locPos);

		float sample = 1. - smoothstep(0.0, 1.5, centerDistance);
		
		if(sample == 0) {
			continue;
		}
		
		ivec2 point = ivec2(floor(samplePos)) + cell;
		float color = texelFetch(textures[texID_vs], point, 0).w;
		
		value += color * sample;
		accum += sample;
	}
	

	return value/accum;
}