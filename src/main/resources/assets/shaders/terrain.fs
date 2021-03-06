#version 140
#extension GL_ARB_shading_language_420pack : enable

out vec4 color_out;
in float normal_vs;
in vec3 pos_vs;
vec3 getColor(vec2 v);
int getID(vec2 v);
vec3 iqnoise(vec2 pos);

layout(binding = 0) uniform sampler2D texIDs;
layout(binding = 1) uniform sampler1D texBiomes;
layout(binding = 2) uniform sampler2D texTerr;

float rand(vec2 p) {
	return fract(sin(dot(p,vec2(419.2,371.9))) * 833458.57832);
}

void main(void) {
	vec2 pos = fract(pos_vs.xz / 32.) * 32.;
	vec3 color = iqnoise(pos);
 	color_out = vec4(color, 1);

	float h = (((pos_vs.y / 2.) * (pos_vs.y / 2.)) / 150.) - 1;
	
	color_out *= texture(texTerr, pos / 2);
	
	if(h < 0.8) {
		color_out.b += (h / 2) + 0.5;
	}
		
	color_out *= normal_vs;
	color_out.a = 1;
}

vec3 iqnoise(vec2 pos) {
	ivec2 cell = ivec2(floor(pos));
	vec2 cellOffset = fract(pos);
	
	vec3 value = vec3(0);
	float accum = 0.;
	
	//int i = getID(cell);
	
	//if(i == getID(cell + ivec2(1, 0)) && i == getID(cell + ivec2(0, -1)) && i == getID(cell + ivec2(-1, 0)) && i == getID(cell + ivec2(0, 1))) {
	//	return getColor(cell);
	//}
	
	for(int x=-1; x<=1; x++)
	for(int y=-1; y<=1; y++)
	{
		vec2 samplePos = vec2(float(x) + 0.5, float(y) + 0.5);

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

vec3 getColor(vec2 v) {
	return texelFetch(texBiomes, getID(v), 0).rgb;
}

int getID(vec2 v) {
	return int(texelFetch(texIDs, ivec2(v + 1), 0).r * 255.);
}