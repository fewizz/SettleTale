#version 140
#extension GL_ARB_shading_language_420pack : enable

float iqnoise(vec3 pos);
float rand(vec3 p);

out vec4 color;

layout (binding = 0, std140) uniform Transform
{
	mat4 projMat;
	mat4 viewMat;
};

layout (binding = 1, std140) uniform DSize
{
	float w;
	float h;
} size;

void main(void) {
	vec4 w = gl_FragCoord;
	vec3 ndc = vec3(
					((w.x * 2) - size.w) / size.w,
					((w.y * 2) - size.h) / size.h,
					1
					);
	
	vec4 clip;
	clip.xyz = ndc.xyz * w.w;
	clip.w = 1;
	
	vec4 front = inverse(projMat) * clip;
	front.xyz = front.xyz * front.w;
	front.w = 1;
	front.xyz *= 1000 / length(front.xyz);
	
	/*mat4 rotate = inverse(viewMat);
	rotate[3][0] = 0;
	rotate[3][1] = 0;
	rotate[3][2] = 0;
	rotate[3][3] = 1;

	vec4 rotated = rotate * front;*/
	
	vec4 eye = inverse(viewMat) * front;
	///////////////////////////////////////
	
	eye.xyz = eye.xyz * eye.w;
	eye.w = 1;
	
	float ay = abs(eye.y);
	
	color = vec4(0, eye.y > 0 ? 1 - sqrt(ay / 1500.) : 1, 1, 1);
}

float iqnoise(vec3 pos) {
	vec3 cell = floor(pos);
	vec3 cellOffset = fract(pos);
	
	float value = 0.0;
	float accum = 0.0;
	
	for(int x=-2; x<=2; x++ )
	for(int y=-2; y<=2; y++ )
	for(int z=-2; z<=2; z++ )
	{
		vec3 samplePos = vec3(float(y), float(x), float(z));

		float centerDistance = length(samplePos - cellOffset);

		float sample = 1.0 - smoothstep(0.0, 1.414, centerDistance);

		float color = rand(cell + samplePos);
		value += color * sample;
		accum += sample;
	}

	return value/accum;
}

float rand(vec3 p) {
	return fract(sin(dot(p,vec3(419.2,371.9,258.3))) * 833458.57832);
}