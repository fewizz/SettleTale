#version 430 core

out vec4 color_out;
in vec3 normal_vs;
in vec3 pos_vs;

layout(binding = 0) uniform sampler2D tex;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
} transf;

layout (binding = 1, std140) uniform DSize {
	float w;
	float h;
} size;

void main(void) {
	color_out = texture(tex, vec2((pos_vs.x + 1) / 18., (pos_vs.z + 1) / 18.));

	float h = (((pos_vs.y / 2) * (pos_vs.y / 2)) / 150.) - 1;
	
	color_out *= h - 0.6;
	
	if(h < 0.8) {
		color_out.b = 1;
		color_out.r *= h;
		color_out.g *= h;
	}
	
		
	color_out *= normal_vs.y;
}