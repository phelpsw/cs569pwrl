#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float exponent;
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
	float nDotL = dot(nNormal, nLightVector);

	if (nDotL > 0.0) {
		vec3 reflected = reflect(-nLightVector, nNormal);
		float specular = pow(max(dot(reflected, nEyeVector), 0.0), exponent);
		colorValue += (diffuseColor * nDotL + specularColor * specular) 
			* calculateShadow(shadowTexture, fragPos);
	}
	
	gl_FragColor = colorValue;

}
