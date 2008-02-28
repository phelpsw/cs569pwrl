#version 110

/* Uniform inputs */
uniform vec4 specularColor;
uniform float eta;
uniform float roughness;
uniform sampler2D diffuseTexture;
/*uniform sampler2D axisTexture;
uniform sampler2D betaTexture;
uniform sampler2D fiberTexture;
*/

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector, normal;
varying vec2 texCoord;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */
	vec4 texDiffuseColor = texture2D(diffuseTexture, texCoord);
	
	float nDotL = dot(nNormal, nLightVector);

	gl_FragColor = colorValue;
}