#version 110

/* Uniform inputs */
uniform vec4 baseColor;
uniform vec4 glowColor;
uniform float glowFactor;
uniform sampler2D glowFilterTexture;
uniform sampler2DShadow shadowTexture;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector, normal;
varying vec4 fragPos;

float calculateShadow(sampler2DShadow shadowTexture, vec4 fragPos) {
	vec4 pos = 0.5 * (fragPos / fragPos.w + 1.0);
	if (pos.x < 0.0 || pos.x > 1.0 || pos.y < 0.0 || pos.y > 1.0)
		return 0.0;
	return shadow2D(shadowTexture, pos.xyz).r;
}

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */

	/* Diffuse term */
	float nDotL = max(0.0, dot(nNormal, nLightVector));

	colorValue += (baseColor * nDotL + glowFactor * glowColor * texture2D(glowFilterTexture, gl_TexCoord[0].xy)) 
			* calculateShadow(shadowTexture, fragPos);

	/* Some ambient, too */
	gl_FragColor = colorValue;
}
