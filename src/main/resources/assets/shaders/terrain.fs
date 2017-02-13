#version 140
#extension GL_ARB_shading_language_420pack : enable

out vec4 color_out;
in float normal_vs;
in vec3 pos_vs;
vec3 getColor(vec2 v);

layout(binding = 0) uniform sampler2D texIDs;
layout(binding = 1) uniform sampler1D texBiomes;
layout(binding = 2) uniform sampler2D texTerr;

float rand(vec2 p) {
	return fract(sin(dot(p,vec2(419.2,371.9))) * 833458.57832);
}

vec3 iqnoise(vec2 pos) {
	ivec2 cell = ivec2(floor(pos));
	vec2 cellOffset = fract(pos);
	
	vec3 value = vec3(0);
	float accum = 0.;
	
	for(float x=-0.5; x<=1.5; x++)
	for(float y=-0.5; y<=1.5; y++)
	{
		vec2 samplePos = vec2(x, y);

		vec2 locPos = samplePos - cellOffset;
		float centerDistance = length(locPos);

		float sample = 1. - smoothstep(0.0, 1.5, centerDistance);
		
		if(sample == 0) {
			continue;
		}
		
		ivec2 point = ivec2(floor(samplePos)) + cell;
		vec3 color = getColor(cell + samplePos);
		
		value += color * sample;
		accum += sample;
	}

	return value/accum;
}

void main(void) {
	vec2 pos = fract(pos_vs.xz / 16.) * 16.;
	vec3 color = iqnoise(pos);
 	color_out = vec4(color, 1);

	float h = (((pos_vs.y / 2) * (pos_vs.y / 2)) / 150.) - 1;
	
	color_out *= texture(texTerr, pos / 2);
	
	if(h < 0.8) {
		color_out.b += (h / 2) + 0.4;
	}
		
	color_out *= normal_vs;
	color_out.a = 1;
}

vec3 getColor(vec2 v) {
	return texelFetch(	texBiomes, int(texelFetch(texIDs, ivec2(v + 1), 0).r * 255.), 0).rgb;
}