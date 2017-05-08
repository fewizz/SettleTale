#version 330
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

in vec3 normal_vs;
in vec2 uv_vs;
flat in int hasUV;
flat in int hasNormal;
flat in int matID;
out vec4 color_out;

layout (location = 0) uniform sampler2D diffTextures[16];

struct MaterialStruct {
	vec4 diffuseColor;
};

layout (binding = 4, std140) uniform Material {
	MaterialStruct materials[16];
};

void main(void) {
	
	if(hasUV == 1) {
		if(hasNormal == 1) {
			vec4 tex = texture(diffTextures[matID], uv_vs);
			color_out = vec4(tex.xyz * normal_vs.y, tex.a) * materials[matID].diffuseColor;
		}
		else {
			color_out = texture(diffTextures[matID], uv_vs) * materials[matID].diffuseColor;
		}
	}
	else {
		if(hasNormal == 1) {
			color_out = vec4(1);//materials[matID].diffuseColor.xyz * normal_vs.y, materials[matID].diffuseColor);
		}
		else {
			color_out = materials[matID].diffuseColor;
		}
	}
	
	if(color_out.a == 0) discard;
	//color_out = vec4(1);
}