#version 110

uniform samplerCube cubeMap;

/* Inputs <- Vertex program */
varying vec3 eyeVector, normal;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	
	vec3 reflected = reflect(nEyeVector, nNormal);

	gl_FragColor = textureCube(cubeMap, reflected);
}