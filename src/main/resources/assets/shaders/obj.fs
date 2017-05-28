#version 330
#extension GL_ARB_shading_language_420pack : enable
#extension GL_ARB_explicit_uniform_location : enable

in vec3 normal_vs;
in vec2 uv_vs;
flat in int flags_vs;
out vec4 color_out;

layout (location = 0) uniform sampler2D diffTextures[32];
layout (location = 32) uniform sampler2D bumpTextures[32];

struct MaterialStruct {
	vec4 diffuseColor;
};

layout (binding = 4, std140) uniform Material {
	MaterialStruct materials[32];
};

void main(void) {
	int matID = flags_vs & 0xFF;
	int hasUV = (flags_vs >> 8) & 0x1;
	
	if(hasUV == 1) {
		vec4 tex = texture(diffTextures[matID], uv_vs);
		
		float dotBump = texture(bumpTextures[matID], uv_vs).y;
		
		color_out = vec4(tex.xyz * dotBump * normal_vs.y, tex.a);// * materials[matID].diffuseColor;
		
		
		//}
		//else {
		//	color_out = texture(diffTextures[matID], uv_vs) * materials[matID].diffuseColor;
		//}
	}
	else {
		//if(hasNormal == 1) {
		
		color_out = vec4(materials[matID].diffuseColor.xyz * normal_vs.y, materials[matID].diffuseColor);
			
		//}
		//else {
		//	color_out = materials[matID].diffuseColor;
		//}
	}
	
	if(color_out.a == 0) discard;
}