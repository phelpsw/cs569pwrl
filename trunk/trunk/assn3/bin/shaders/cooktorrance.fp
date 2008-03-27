#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;

/* Schlick approx.: Fresnel reflection factor at normal incidence
	   R0 = (1-eta)^2/(1+eta)^2
*/
uniform float r0; 

/* RMS slope of the microfacets */
uniform float m;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector;
varying vec3 normal;

void main() {
	vec4 finalColor = vec4(0.0,0.0,0.0,0.0);

	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);

	/* Half-way vector */
	vec3 halfVector = normalize(nEyeVector + nLightVector);

	float nDotL = dot(nNormal, nLightVector);

	if (nDotL > 0.0) {
		float nDotH = max(0.0, dot(nNormal, halfVector));
		float nDotV = max(0.0, dot(nNormal, nEyeVector));
		float hDotV = max(0.0, dot(halfVector, nEyeVector));

		/* Beckmann microfacet distribution */
		float tanAlpha = sqrt(1.0-nDotH*nDotH)/nDotH;

		float D = exp(-pow(tanAlpha/m,2.0))/(pow(m,2.0)*pow(nDotH,4.0));

		/* Shadowing-masking term */
		float G1 = (2.0 * nDotH * nDotV) / hDotV;
		float G2 = (2.0 * nDotH * nDotL) / hDotV;
		float G = min(1.0, min(G1, G2));

		/* Fresnel specular reflection coefficient (Schlick approx.) */
		float F = r0 + (1.0 - r0) * pow(1.0 - nDotL, 5.0);

		float R = (F*G*D) / (nDotL * nDotV);
		finalColor = (specularColor * R + diffuseColor) * nDotL;
	}

	/* Some ambient */
	gl_FragColor = finalColor + 0.05;
}
