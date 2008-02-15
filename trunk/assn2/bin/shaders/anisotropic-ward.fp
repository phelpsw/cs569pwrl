#version 110

/* Uniform inputs */
uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float alphaX;
uniform float alphaY;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector;
varying vec3 vNormal, vTangent, vBinormal;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 normal = normalize(vNormal);
	vec3 tangent = normalize(vTangent);
	vec3 binormal = normalize(vBinormal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);

	/* Half-way vector */
	vec3 halfVector = normalize(nEyeVector + nLightVector);

	float nDotL = max(0.0, dot(normal, nLightVector));
	float nDotV = dot(normal, nEyeVector);
	float nDotH = dot(normal, halfVector);

	float term1 = 1.0/(4.0 * alphaX * alphaY);
	float term2 = nDotL * nDotV;
	if (term2 > 0.0)
		term2 == 1.0 / sqrt(term2);

	float termX = dot(halfVector, tangent) / alphaX;
	float termY = dot(halfVector, binormal) / alphaY;
	float expTerm = -(termX*termX  + termY*termY) / (nDotH*nDotH);

	float specular = term1*term2*exp(expTerm);

	gl_FragColor.rgb = (diffuseColor.rgb + specularColor.rgb * specular)
		* nDotL + 0.05;
}
