#version 140
#extension GL_ARB_shading_language_420pack : enable

out vec4 color_out;
in float normal_vs;
in vec3 pos_vs;
vec4 getColor(int x, int z, float scale);

layout(binding = 0) uniform sampler2D texIDs;
layout(binding = 1) uniform sampler1D texBiomes;
layout(binding = 2) uniform sampler2D texTerr;

void main(void) {
	float xr = pos_vs.x / 16.;
	float zr = pos_vs.z / 16.;

	float x2 = pos_vs.x - floor(pos_vs.x); // 31.1 - 31 = 0.1
	float z2 = pos_vs.z - floor(pos_vs.z);
	
	float xp = xr - floor(xr);
	float zp = zr - floor(zr);
	xp *= 16.;
	zp *= 16.;
	x2-=0.5;
	z2-=0.5;
	
	int x = int(xp);
	int z = int(zp);
	
	float ax = abs(x2);
 	float az = abs(z2);
	
	float ls = -x2 * (1 - az);
 	float lus = -x2 * z2;
 	float us = (1 - ax) * z2;
 	float urs = x2 * z2;
 	float rs = x2 * (1 - az);
 	float rds = x2 * -z2;
 	float ds = (1 - ax) * -z2;
 	float dls = -x2 * -z2;
 	
 	if(x2 < 0) {
 		urs = 0;
 		rs = 0;
 		rds = 0;
 	}
 	if(x2 > 0) {
 		lus = 0;
 		ls = 0;
 		dls = 0;
 	}
 	if(z2 < 0) {
 		lus = 0;
 		us = 0;
 		urs = 0;
 	}
 	if(z2 > 0) {
 		rds = 0;
 		ds = 0;
 		dls = 0;
 	}
 	
 	color_out = getColor(x - 1, z, ls) + getColor(x - 1, z + 1, lus) + getColor(x, z + 1, us) + getColor(x + 1, z + 1, urs) +
 				getColor(x + 1, z, rs) + getColor(x + 1, z - 1, rds) + getColor(x, z - 1, ds) + getColor(x - 1, z - 1, dls);
 				
 	color_out += getColor(x, z, ((1 - ax) * (1 - az)));

	float h = (((pos_vs.y / 2) * (pos_vs.y / 2)) / 150.) - 1;
	
	color_out *= texture(texTerr, vec2(xr, zr));
	color_out *= h - 0.1;
	
	if(h < 0.8) {
		color_out.b += (h / 2) + 0.4;
	}
		
	color_out *= normal_vs;
}

vec4 getColor(int x, int z, float scale) {
	if(scale == 0) {
		return vec4(0.0);
	}
	return texelFetch(	texBiomes, int(texelFetch(texIDs, ivec2(x + 1, z + 1), 0).r * 255.), 0) * scale;
}