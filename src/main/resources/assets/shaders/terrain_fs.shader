#version 430 core

out vec4 color_out;
in vec4 color_vs;
in vec3 normal_vs;
in float y_vs;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
} transf;

layout (binding = 1, std140) uniform DSize {
	float w;
	float h;
} size;

void main(void) {
	float h = ((((y_vs / 2) * (y_vs / 2)) / 150.) - 1);
	
	color_out = color_vs;
	
	if(h < 0.8) {
		color_out.b = 1;
		color_out.r *= h;
		color_out.g *= h;
	}
		
	color_out *= normal_vs.y;
}