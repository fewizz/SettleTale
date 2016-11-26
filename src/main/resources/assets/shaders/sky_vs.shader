#version 450 core

layout (binding = 0, std140) uniform Transform {
	mat4 projMat;
	mat4 viewMat;
} transf;

//out vec4 posTransf;

void main (void) {
	const vec4[4] vecs = vec4[4](
		vec4(-1, -1, 0.9999999, 1),
		vec4( 1, -1, 0.9999999, 1),
		vec4( 1,  1, 0.9999999, 1),
		vec4(-1,  1, 0.9999999, 1)
	);
	vec4 vec = vecs[gl_VertexID];
	gl_Position = vec;
	//posTransf = transf.viewMat * vec;
}