#version 110

/* Uniform inputs */
uniform vec4 diffuseColor, specularColor;
uniform float n, m;

/* Inputs <- Vertex program */
varying vec3 lightVector, normal, eyeVector;

void main() {

    /* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nLightVector = normalize(lightVector);
	vec3 nEyeVector = normalize(eyeVector);
	
	vec3 nHalf = normalize(nLightVector + nEyeVector);
    float nDotH = dot(nNormal, nHalf);
    float vDotH = dot(nEyeVector, nHalf);
    float nDotV = dot(nNormal, nEyeVector);
    
    float G = min(1.0, min(2.0*nDotH*nDotV/vDotH, 2.0*nDotH*dot(nNormal, nLightVector)/vDotH));
        
    float Ro = ((1.0-n)/(1.0+n))*((1.0-n)/(1.0+n));
    float F = Ro + (1.0-Ro)*pow(1.0-vDotH, 5.0);
    
    //Note: tan^2 = 1/cos^2 - 1
    float tanASquared = 1.0/(nDotH*nDotH) - 1.0;
    float D = exp( -tanASquared/(m*m) ) / (4.0*m*m*pow(nDotH, 4.0));
    //float D = exp( -(1.0-nDotH*nDotH)/(m*m*nDotH*nDotH) ) / (4.0*m*m*pow(nDotH, 4.0));
	
	/* Diffuse color */
	vec4 diffuse = diffuseColor * max(0.0, dot(nNormal, nLightVector));
		
	vec4 specular = specularColor * max(0.0, D * F * G / (3.14159 * nDotV)); 
	
	gl_FragColor = diffuse + specular + 0.05;	
}
