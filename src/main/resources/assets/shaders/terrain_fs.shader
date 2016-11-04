#version 430 core

out vec4 color_out;
in vec4 color;
in vec3 normal;
in float y;
in vec4 pos;

layout (binding = 0, std140) uniform Transform
{
	mat4 projMat;
	mat4 viewMat;
} transf;

layout (binding = 1, std140) uniform DSize
{
	float w;
	float h;
} size;

void main(void) {
	// from windows space to world space //
	vec4 w = gl_FragCoord;
	vec3 ndc = vec3(
					((w.x * 2) - size.w) / size.w,
					((w.y * 2) - size.h) / size.h,
					(w.z * 2) - 1
					);
					
	vec4 clip;
	clip.w = 1. / w.w;
	clip.xyz = ndc * clip.w;

	vec4 eye = inverse(transf.projMat) * clip;
	eye = inverse(transf.viewMat) * eye;
	///////////////////////////////////////
	
	float h = ((((eye.y / 2) * (eye.y / 2)) / 150.) - 1);
	
	color_out = color * h;
	
	if(h < 0.8)
		color_out.b = 1;
		
}