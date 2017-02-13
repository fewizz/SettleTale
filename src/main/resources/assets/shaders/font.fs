#version 140
#extension GL_ARB_shading_language_420pack : enable

layout(binding = 0) uniform sampler2D tex;

out vec4 color_out;
in vec4 color_vs;
in vec2 uv_out;
float iqnoise(vec2 pos);

void main(void) {
	vec2 uv = uv_out * textureSize(tex, 0);
	color_out = color_vs * smoothstep(0.5, 0.5, iqnoise(uv));
}

float iqnoise(vec2 pos) {
	ivec2 cell = ivec2(floor(pos));
	vec2 cellOffset = fract(pos);
	
	float value = 0.;
	float accum = 0.;
	
	const float min = -.5;
	const float max = 1.5;
	
	for(float x = min; x<=max; x++)
	for(float y = min; y<=max; y++)
	{
		vec2 samplePos = vec2(x, y);
		vec2 locPos = samplePos - cellOffset;

		float centerDistance = length(locPos);

		float sample = 1. - smoothstep(0.0, 1.5, centerDistance);
		
		if(sample == 0) {
			continue;
		}
		
		ivec2 point = ivec2(floor(samplePos)) + cell;
		float color = texelFetch(tex, point, 0).w;
		
		value += color * sample;
		accum += sample;
	}
	

	return value/accum;
}