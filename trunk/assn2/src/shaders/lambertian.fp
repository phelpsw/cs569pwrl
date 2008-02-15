#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;

/* Inputs <- Vertex program */
varying vec3 lightVector, normal;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nLightVector = normalize(lightVector);

	/* Diffuse term */
	float nDotL = max(0.0, dot(nNormal, nLightVector));

	/* Some ambient, too */
	gl_FragColor = diffuseColor * nDotL + 0.05;
}
