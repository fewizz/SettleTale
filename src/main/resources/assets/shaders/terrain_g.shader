#version 420 core

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

out vec4 color;
out vec3 normal;
out vec4 pos;
in vec4 color_vs[];

layout (binding = 0, std140) uniform Transform
{
	mat4 projMat;
	mat4 viewMat;
} transf;

void main(void) {
	mat4 vmt = transf.projMat * transf.viewMat;

	vec3 v1 = gl_in[0].gl_Position.xyz;
	vec3 v2 = gl_in[1].gl_Position.xyz;
	vec3 v3 = gl_in[2].gl_Position.xyz;
	
	vec3 n1 = normalize(cross(v1 - v2, v1 - v3));
	
	pos = gl_in[0].gl_Position;
	normal = n1;
	color = color_vs[0];
	gl_Position = vmt * gl_in[0].gl_Position;
	EmitVertex();
	
	pos = gl_in[1].gl_Position;
	normal = n1;
	color = color_vs[1];
	gl_Position = vmt * gl_in[1].gl_Position;
	EmitVertex();
	
	pos = gl_in[2].gl_Position;
	normal = n1;
	color = color_vs[2];
	gl_Position = vmt * gl_in[2].gl_Position;
	EmitVertex();
}