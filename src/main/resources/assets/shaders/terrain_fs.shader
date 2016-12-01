#version 430 core

out vec4 color_out;
in vec3 normal_vs;
in vec3 pos_vs;

flat in float id_vs;
flat in vec4 ids1_vs;
flat in vec4 ids2_vs;

layout(binding = 0) uniform sampler1D tex;

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
} transf;

layout (binding = 1, std140) uniform DSize {
	float w;
	float h;
} size;

void main(void) {
	float x = pos_vs.x - float(int(pos_vs.x));
	float z = pos_vs.z - float(int(pos_vs.z));
	
	if(x < 0)
		x = 1 + x;
	if(z < 0)
		z = 1 + z;
	
	x -= 0.5;
		
	z -= 0.5;
		
	float ax = abs(x);
	float az = abs(z);
	
	float ls = -x * (1 - az);
	float lus = -x * z;
	float us = (1 - ax) * z;
	float urs = x * z;
	float rs = x * (1 - az);
	float rds = x * -z;
	float ds = (1 - ax) * -z;
	float dls = -x * -z;
	
	if(x < 0) {
		urs = 0;
		rs = 0;
		rds = 0;
	}
	if(x > 0) {
		lus = 0;
		ls = 0;
		dls = 0;
	}
	if(z < 0) {
		lus = 0;
		us = 0;
		urs = 0;
	}
	if(z > 0) {
		rds = 0;
		ds = 0;
		dls = 0;
	}

	vec4 color = (texture(tex, ids1_vs[0]) * ls) + (texture(tex, ids1_vs[1]) * lus) + (texture(tex, ids1_vs[2]) * us) + (texture(tex, ids1_vs[3]) * urs)
			   + (texture(tex, ids2_vs[0]) * rs) + (texture(tex, ids2_vs[1]) * rds) + (texture(tex, ids2_vs[2]) * ds) + (texture(tex, ids2_vs[3]) * dls);
	color += texture(tex, id_vs) * ((1 - ax) * (1 - az));
	color_out = color;

	float h = (((pos_vs.y / 2) * (pos_vs.y / 2)) / 150.) - 1;
	
	color_out *= h - 0.6;
	
	if(h < 0.8) {
		color_out.b = 1;
		color_out.r *= h;
		color_out.g *= h;
	}
	
		
	color_out *= normal_vs.y;
}