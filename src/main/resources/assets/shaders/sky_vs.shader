#version 130

void main (void) {
	const vec4[4] vecs = vec4[4](
		vec4(-1, -1, 0.9999999, 1),
		vec4( 1, -1, 0.9999999, 1),
		vec4( 1,  1, 0.9999999, 1),
		vec4(-1,  1, 0.9999999, 1)
	);
	vec4 vec = vecs[gl_VertexID];
	gl_Position = vec;
}