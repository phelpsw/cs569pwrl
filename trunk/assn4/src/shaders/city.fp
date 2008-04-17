#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float exponent;
uniform sampler2D diffuseMap;

/* Inputs <- Vertex program */
varying vec3 lightVector1, lightVector2, lightVector3, lightVector4, eyeVector, normal;

vec4 phong(vec3 lightVector, vec3 nNormal, vec3 nEyeVector, vec4 textureValue) {
	vec3 nLightVector = normalize(lightVector);
	float nDotL = dot(nNormal, nLightVector);

	if (nDotL > 0.0) {
		vec3 reflected = reflect(-nLightVector, nNormal);
		float specular = pow(max(dot(reflected, nEyeVector), 0.0), exponent);
		return diffuseColor * textureValue
			 * nDotL;// + specularColor * specular;
	} else {
		return vec4(0.0);
	}
}

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */
	vec4 textureValue = texture2D(diffuseMap, gl_TexCoord[0].xy);

	if (textureValue.a < 0.5) {
		discard;
	} else {
		colorValue += phong(lightVector1, nNormal, nEyeVector, textureValue);
		colorValue += phong(lightVector2, nNormal, nEyeVector, textureValue);
		colorValue += phong(lightVector3, nNormal, nEyeVector, textureValue);
		colorValue += phong(lightVector4, nNormal, nEyeVector, textureValue);
	}

	gl_FragColor = colorValue / 3.0;
}
