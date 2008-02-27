#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float exponent;
uniform sampler2DShadow shMap;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector, normal;
varying vec3 shCoord;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */
	
	vec3 nShCoord = normalize(shCoord);
	
	float shDepth = shadow2D(shMap, nShCoord).r;
	
	float nDotL = dot(nNormal, nLightVector);

	if (nDotL > 0.0 && shDepth > 0.0) {
		vec3 reflected = reflect(-nLightVector, nNormal);
		float specular = pow(max(dot(reflected, nEyeVector), 0.0), exponent);
		colorValue += diffuseColor * nDotL + specularColor * specular;
	}

	gl_FragColor = colorValue;
}