#version 110

/* Uniform inputs */
uniform vec4 baseColor;
uniform vec4 glowColor;
uniform float glowFactor;
uniform sampler2D glowFilterTexture;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector, normal;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */

	/* Diffuse term */
	float nDotL = max(0.0, dot(nNormal, nLightVector));

	colorValue += baseColor * nDotL;

	colorValue += glowFactor * glowColor * texture2D(glowFilterTexture, gl_TexCoord[0].xy);

	/* Some ambient, too */
	gl_FragColor = colorValue;
}
