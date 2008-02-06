#version 110

/* Uniform inputs */
uniform vec4 diffuseColor, specularColor;
uniform float exponent;

/* Inputs <- Vertex program */
varying vec3 lightVector, normal, eyeVector;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nLightVector = normalize(lightVector);
	vec3 nEyeVector = normalize(eyeVector);

	/* Diffuse color */
	vec4 diffuse = diffuseColor * max(0.0, dot(nNormal, nLightVector));
	
	vec3 rVector = 2.0*dot(nNormal, nLightVector)*nNormal - nLightVector;
	vec4 specular = specularColor * pow(max(0.0, dot(nEyeVector, rVector)), exponent); 
	
	gl_FragColor = diffuse + specular + 0.05;
	/*	gl_FragColor.rgb = vec3(0.2, 0.2, 0.5); */
}

