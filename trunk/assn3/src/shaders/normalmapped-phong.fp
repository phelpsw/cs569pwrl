#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float exponent;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector, normal;
varying vec3 vNormal, vTangent, vBinormal;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */

	/* Look up the normal in the normal map */
	vec3 texNormal = normalize(texture2D(normalMap, gl_TexCoord[0].xy).xyz - 0.5);
	
	/* Look up the diffuse color */
	vec4 baseColor = diffuseColor * texture2D(diffuseMap, gl_TexCoord[0].xy);

	float nDotL = dot(texNormal, nLightVector);
	if (nDotL > 0.0) {
		vec3 reflected = reflect(-nLightVector, texNormal);
		float specular = pow(max(dot(reflected, nEyeVector), 0.0), exponent);
		colorValue += baseColor * nDotL + specularColor * specular;
	}

	gl_FragColor = colorValue;
}