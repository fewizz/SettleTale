#version 140
#extension GL_ARB_shading_language_420pack : enable

out vec4 color_out;
in vec3 normal_vs;
in vec3 pos_vs;

layout(binding = 0) uniform sampler2D tex;

void main(void) {
	float xr = pos_vs.x / 16.;
	float zr = pos_vs.z / 16.;

	float x = xr - floor(xr);
	float z = zr - floor(zr);
	
	x -= 0.5;
	z -= 0.5;
	x *= 16. / 18.;
	z *= 16. / 18.;
	x += 0.5;
	z += 0.5;

	color_out = texture(tex, vec2(x, z));

	float h = (((pos_vs.y / 2) * (pos_vs.y / 2)) / 150.) - 1;
	
	if(h < 0.8) {
		color_out.b = 1;
		color_out.r *= h;
		color_out.g *= h;
	}
	
		
	color_out *= normal_vs.y;
}