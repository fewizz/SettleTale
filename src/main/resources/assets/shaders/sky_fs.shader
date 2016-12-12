#version 140
#extension GL_ARB_shading_language_420pack : enable

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
	clip.w = 1. / w.w;
	clip.xyz = ndc.xyz * clip.w;
	
	vec4 front = inverse(projMat) * clip;
	front /= length(front);

	 vec4 eye = inverse(viewMat) * front;
	///////////////////////////////////////
	
	float yo = abs(eye.y) + 1;
	
	color = vec4(0, (2.6 - yo) / 2, yo * yo, 1);
}