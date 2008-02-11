#version 110

/* Uniform inputs */
uniform vec4 diffuseColor, specularColor;
uniform float alphaX, alphaY;

/* Inputs <- Vertex program */
varying vec3 lightVector, normal, eyeVector, tan, binorm;

void main() {

    /* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(normal);
	vec3 nLightVector = normalize(lightVector);
	vec3 nEyeVector = normalize(eyeVector);
	
	vec3 X = normalize(tan);
	vec3 Y = normalize(binorm);
	
	vec3 nHalf = normalize(nLightVector + nEyeVector);
	float nDotH = dot(nNormal, nHalf);    	
        
	float expTerm = exp( -(pow(dot(nHalf, X)/alphaX, 2.0)+pow(dot(nHalf, Y)/alphaY, 2.0))/(nDotH*nDotH) );
	//float expTerm = exp( -(1.0-nDotH*nDotH)/(alphaX*alphaY*nDotH*nDotH) ); // ~isotropic

 	//Bruce Walter's formula for Ward
    //float coeff = 1.0/( sqrt(dot(nNormal, nLightVector)*dot(nNormal, nEyeVector)) * 4.0*3.14159*alphaX*alphaY);		
	float coeff = sqrt(dot(nNormal, nLightVector)) / ( sqrt(dot(nNormal, nEyeVector)) * 4.0*3.14159*alphaX*alphaY); 
	
	
	/* Diffuse color */
	vec4 diffuse = diffuseColor * max(0.0, dot(nNormal, nLightVector));
		
	vec4 specular = specularColor * coeff * expTerm; // is it necessary to cutoff at 0?   
		
	gl_FragColor = diffuse + specular + 0.05;
}
