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
varying vec3 lightVector, eyeVector;
varying vec2 texCoord;
varying vec3 vNormal, vTangent, vBinormal;

// this is overwriting an existing glsl function, find out what the diff is!!
vec3 faceForward(vec3 N, vec3 I) {
	if(dot(N, I) <= 0.0)
	{
		return N;
	}
	return -1.0*N;
}

void fresnel(in vec3 incom, 
		in vec3 normal, 
		in float eta_val, /* global named eta */
		out float reflectance, out float transmittance, 
		out vec3 reflection, out vec3 refraction) 
{
	//float eta = index_external/index_internal;
	float cos_theta1 = dot(incom, normal);
	float cos_theta2 = sqrt(1.0 - ((eta_val * eta_val) * ( 1.0 - (cos_theta1 * cos_theta1))));
	
	reflection = incom - 2.0 * cos_theta1 * normal; 
	refraction = (eta_val * incom) + (cos_theta2 - eta_val * cos_theta1) * normal;
	
	/*
	float fresnel_rs = (index_external * cos_theta1 - 
						index_internal * cos_theta2 ) / 
						(index_external * cos_theta1 + 
						index_internal * cos_theta2);
	
	float fresnel_rp = (index_internal * cos_theta1 - 
						index_external * cos_theta2 ) / 
						(index_internal * cos_theta1 + 
						index_external * cos_theta2);
	*/				
	float fresnel_rs = (eta_val * cos_theta1 - 
						1.0 * cos_theta2 ) / 
						(eta_val * cos_theta1 + 
						1.0 * cos_theta2);
	
	float fresnel_rp = (1.0 * cos_theta1 - 
						eta_val * cos_theta2 ) / 
						(1.0 * cos_theta1 + 
						eta_val * cos_theta2);
	
	reflectance = (fresnel_rs * fresnel_rs + fresnel_rp * fresnel_rp) / 2.0;
	transmittance =((1.0-fresnel_rs) * (1.0-fresnel_rs) + 
					(1.0-fresnel_rp) * (1.0-fresnel_rp)) / 2.0;
	
	
}

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(vNormal);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);
	vec4 colorValue = vec4(0.05); /* Some ambient */
	vec4 texDiffuseColor = texture2D(diffuseTexture, texCoord);
	
	//float nDotL = dot(nNormal, nLightVector);

	vec3 Nf = faceForward(nNormal, nEyeVector);
	
	vec3 ssInDir, ssOutDir; /* Light and eye vector, possibly refracted */
	float thInPrime, thOutPrime;
	float halfAngle, diffAngle;
	float fiberFactor, geometryFactor;

	float Kr, Kt;                          /* for output of fresnel() */
	float ssAtten; /* Attenuation from going through the smooth interface twice */

	/* Get local coordinate system in terms of native parameters (u,v). */
	/* We should really use (s,t). */
	vec3 local_z = Nf;
  
	/* Get unit vector in "u" parameter direction */
	vec3 local_x = normalize (dFdx(vTangent));
	/* Get final local basis vector y perpendicular to x and z. */
	vec3 local_y = cross(local_z, local_x);

	if ( eta != 1.0 ) {
		vec3 Rdir;                     /* Dummy */
		fresnel(nEyeVector, Nf, 1.0/eta, Kr, Kt, Rdir, ssOutDir);
		
	} else {
		ssOutDir = -1.0 * nEyeVector;
		ssAtten = 1.0;
	}



	gl_FragColor = colorValue;
}

