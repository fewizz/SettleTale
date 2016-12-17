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
	vec2 cell = floor(pos);
	vec2 cellOffset = fract(pos);
	
	vec3 value = vec3(0);
	float accum = 0.0;
	
	for(int x=-1; x<=1; x++ )
	for(int y=-1; y<=1; y++ )
	{
		vec2 samplePos = vec2(float(y), float(x));

		float centerDistance = length(samplePos - cellOffset);

		float sample = 1.0 - smoothstep(0.0, /*1.414*/1.2, centerDistance);

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
	
	color_out *= texture(texTerr, pos);
	
	if(h < 0.8) {
		color_out.b += (h / 2) + 0.4;
	}
		
	color_out *= normal_vs;
}

vec3 getColor(vec2 v) {
	return texelFetch(	texBiomes, int(texelFetch(texIDs, ivec2(v + 1), 0).r * 255.), 0).rgb;
}