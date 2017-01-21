#version 140
#extension GL_ARB_shading_language_420pack : enable

layout(binding = 0) uniform sampler2D tex;

out vec4 color_out;
in vec4 color_vs;
in vec2 uv_out;
float iqnoise(vec2 pos, ivec2 texSize);

void main(void) {
	ivec2 ts = textureSize(tex, 0);
	vec2 uv = uv_out * ts;
	color_out = color_vs * smoothstep(0.5, 0.5, iqnoise(uv, ts));
}

float iqnoise(vec2 pos, ivec2 texSize) {
	vec2 cell = floor(pos);
	vec2 cellOffset = fract(pos);
	
	float value = 0.0;
	float accum = 0.0;
	
	for(int x=-1; x<=1; x++ )
	for(int y=-1; y<=1; y++ )
	{
		vec2 samplePos = vec2(float(y), float(x));

		float centerDistance = length(samplePos - cellOffset);

		float sample = 1.0 - smoothstep(0.0, 1.2, centerDistance);

		ivec2 p = ivec2(cell + samplePos);
		float color = 0;
		if(p.x >= 0 && p.y >= 0 && p.x < texSize.x && p.y < texSize.y) {
			color = texelFetch(tex, p, 0).w;
		}
		value += color * sample;
		accum += sample;
	}

	return value/accum;
}