#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float exponent;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector, normal;
varying vec2 texCoord;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = 2.0*texture2D(normalMap, texCoord).rgb - 1.0;
	nNormal = normalize(gl_NormalMatrix * nNormal);
	
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */
	vec4 texDiffuseColor = diffuseColor * texture2D(diffuseMap, texCoord);
	
	float nDotL = dot(nNormal, nLightVector);

	if (nDotL > 0.0) {
		vec3 reflected = reflect(-nLightVector, nNormal);
		float specular = pow(max(dot(reflected, nEyeVector), 0.0), exponent);
		colorValue += texDiffuseColor * nDotL + specularColor * specular;
	}

	gl_FragColor = colorValue;
}