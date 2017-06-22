#version 140
#extension GL_ARB_shading_language_420pack : enable

float iqnoise(vec3 pos);
float rand(vec3 p);

out vec4 color;

layout (binding = 1, std140) uniform Transform {
	mat4 projMatInv;
	mat4 viewMatInv;
};

layout (binding = 3, std140) uniform Display {
	float w;
	float h;
} size;

void main(void) {
	vec4 w = gl_FragCoord;
	vec3 ndc = vec3(
					((w.x * 2) - size.w) / size.w,
					((w.y * 2) - size.h) / size.h,
					1. / w.z
					);
	vec4 clip = vec4(ndc, 1) / w.w;
	
	vec4 P = projMatInv * clip;
	P /= P.w;
	P.xyz *= 1000 / length(P.xyz);
	
	vec4 eye = viewMatInv * P;
	
	float ay = abs(eye.y);
	
	vec4 skyColor = vec4(0, eye.y > 0 ? 1 - sqrt(ay / 800.) : 1, 1, 1);
	vec3 posForCloud = eye.xyz / 70.;
	posForCloud.y *= 4.;
	float val1 = iqnoise(posForCloud);
	float val2 = iqnoise(posForCloud * 5.F) / 2F;
	float cloud = ((val1 * 1.6) / (ay / 500.)) - 0.3 + (val2 * val1);
	color = skyColor + vec4(cloud);
}

float iqnoise(vec3 pos) {
	ivec3 cell = ivec3(floor(pos));
	vec3 cellOffset = fract(pos);
	
	float value = 0.0;
	float accum = 0.0;
	
	for(float x=-0.5; x<=1.5; x++)
	for(float y=-0.5; y<=1.5; y++)
	for(float z=-0.5; z<=1.5; z++)
	{
		vec3 samplePos = vec3(x, y, z);

		vec3 locPos = samplePos - cellOffset;
		float centerDistance = length(locPos);

		float sample = 1. - smoothstep(0.0, 1.5, centerDistance);
		
		ivec3 point = ivec3(floor(samplePos)) + cell;
		float color = rand(cell + samplePos);
		
		value += color * sample;
		accum += sample;
	}

	return value/accum;
}

float rand(vec3 p) {
	return fract(sin(dot(p,vec3(419.2,371.9,258.3))) * 833458.57832);
}