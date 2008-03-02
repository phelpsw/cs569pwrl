#version 110

/* Uniform inputs */
uniform vec4 specularColor;
uniform float eta;   // n2/n1
uniform float roughness;
uniform sampler2D diffuseTexture;
uniform sampler2D axisTexture;
uniform sampler1D betaTexture;
uniform sampler2D fiberTexture;


/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector;
varying vec2 texCoord;
varying vec3 vNormal, vTangent, vBinormal;

float sqrt2pi = 2.5066283;

// this is overwriting an existing glsl function, find out what the diff is!!
// assumes I vector is pointing toward the surface (renderman convention)
// faceForward returns a vector that points opposite to I
vec3 faceForward(vec3 N, vec3 I) {
	if(dot(N, I) <= 0.0)
	{
		return N;
	} else {
		return -1.0*N;
	}
}

// incom is the eye vector pointing to surface
void fresnel(in vec3 incom, 
		in vec3 normal, 
		in float eta_val, /* global named eta */
		out float reflectance, out float transmittance, 
		out vec3 reflection, out vec3 refraction) 
{
	//float eta = index_external/index_internal;
	float cos_theta1 = -1.0*dot(incom, normal);
	float cos_theta2 = sqrt(1.0 - ((eta_val * eta_val) * ( 1.0 - (cos_theta1 * cos_theta1))));
	
	reflection = incom + 2.0 * cos_theta1 * normal; 
	refraction = (eta_val * incom) + (eta_val * cos_theta1 - cos_theta2) * normal; // (T vector)
	
	
	/*	
	float fresnel_rs = (eta_val * cos_theta1 - 
						1.0 * cos_theta2 ) / 
						(eta_val * cos_theta1 + 
						1.0 * cos_theta2);
	
	float fresnel_rp = (1.0 * cos_theta1 - 
						eta_val * cos_theta2 ) / 
						(1.0 * cos_theta1 + 
						eta_val * cos_theta2);
						*/
						
	float fresnel_rs = (cos_theta1 - 
						eta_val * cos_theta2 ) / 
						(cos_theta1 + 
						eta_val * cos_theta2);
	
	float fresnel_rp = (cos_theta2 - 
						eta_val * cos_theta1 ) / 
						(cos_theta2 + 
						eta_val * cos_theta2);
	
	reflectance = (fresnel_rs * fresnel_rs + fresnel_rp * fresnel_rp) / 2.0;
	//reflectance = 0.3;
	
	transmittance =((1.0-fresnel_rs) * (1.0-fresnel_rs) + 
					(1.0-fresnel_rp) * (1.0-fresnel_rp)) / 2.0;
	
	
}

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nNormal = normalize(vNormal);
	vec3 nLightVector = normalize(lightVector);
	vec3 nEyeVector = normalize(eyeVector);
	vec3 I = -nEyeVector; // Renderman I vector points towards the surface
	vec4 colorValue = vec4(0.05); /* Some ambient */

	vec3 Nf = faceForward(nNormal, I);
	
	vec3 ssInDir, ssOutDir; /* Light and eye vector, possibly refracted */
	float thInPrime, thOutPrime;
	float halfAngle, diffAngle;
	float fiberFactor, geometryFactor;

	float Kr, Kt;                          /* for output of fresnel() */
	float ssAtten; /* Attenuation from going through the smooth interface twice */

	// Min and Max Texture coord constraints
	float minU = 0.0;
	float maxU = 1.0;
	float minV = 0.0;
	float maxV = 1.0;


	/* Get local coordinate system in terms of native parameters (u,v). */
	/* We should really use (s,t). */
	vec3 local_z = Nf;
  
	/* Get unit vector in "u" parameter direction */
	vec3 local_x = normalize (vTangent);
	/* Get final local basis vector y perpendicular to x and z. */
	vec3 local_y = cross(local_z, local_x);

	if ( eta != 1.0 ) {
		vec3 Rdir;                     /* Dummy */
		fresnel(I, Nf, 1.0/eta, Kr, Kt, Rdir, ssOutDir);
		ssOutDir = -1.0 * ssOutDir;
		// Use (1-Kr) rather than Kt, because we are dealing with power,
	    // not radiance.
	    ssAtten = 1.0 - Kr;
	} else {
		ssOutDir = -1.0 * I; // back to nEyeVector
		ssAtten = 1.0;
	}
	normalize(ssOutDir);
	
	// these vals might not be necessary with our lookup approach in glsl
	float ourS = minU + texCoord.x * (maxU - minU);
	float ourT = minV + texCoord.y * (maxV - minV);

	vec4 diffuseColor = texture2D(diffuseTexture, texCoord);
	vec4 highlight = texture2D(fiberTexture, texCoord) * 20.0;
	
	vec4 axisTemp = texture2D(axisTexture, texCoord)*2.0 - 1.0;
	// take perspective (w term) into account before proceeding?
	
    /* Transform to local coordinate system */
    vec3 axis = axisTemp.x * local_x - axisTemp.y * local_y + axisTemp.z * local_z;
	
	// This is almost certainly wrong, I dont think we want a Proj lookup although
	// a normal texture1D takes a float as its param
	float tx_beta = texture1D(betaTexture, texCoord.x).x;
	
	thOutPrime = asin(dot(ssOutDir,axis));
	
	float nDotL = dot(nNormal, nLightVector);
	if(nDotL > 0.0) // cheapo hemisphere detection
	{
		float dummy;
		float ssAttenOut;
		
		
		/* Refract at smooth surface */
   		if ( eta != 1.0 ) {
			vec3 Rdir;				/* dummy */
			fresnel ( -1.0*nLightVector, local_z, 1.0/eta, Kr, Kt, Rdir, ssInDir );
			// Use (1-Kr) rather than Kt, because we are dealing with power,
			// not radiance.
			ssAttenOut = 1.0 - Kr;
   		} else {
   			ssInDir = -1.0 * nLightVector;
   			ssAttenOut = 1.0;
   		}
   		
   		float ssFactor = max ( 0.0, -1.0*dot(ssInDir,local_z) ) * ssAtten*ssAttenOut;
		thInPrime  = asin ( -1.0*dot(ssInDir,axis) );
		halfAngle = thOutPrime + thInPrime;
		diffAngle = thOutPrime - thInPrime;
   		
   		// Compute value of Gaussian at this angle
		fiberFactor = tx_beta * exp ( -1.0*pow(halfAngle/tx_beta,2.0)/2.0) / sqrt2pi;
		float cosIncline = cos(diffAngle / 2.0);
		geometryFactor = 1.0 / pow ( cosIncline, 2.0 );
		fiberFactor *= geometryFactor;
		
		// Add in diffuse term, attenuated by surface term.
		colorValue += specularColor * diffuseColor * ssFactor;
		// Add in fiber highlight, also attenuated.
		colorValue += specularColor * fiberFactor * highlight * ssFactor;
		/* Second Fresnel call is for strength of surface highlight */
		vec3 H = normalize ( nEyeVector + nLightVector ); //half vector
		vec3 dumy1, dumy2;
		fresnel ( I, H, 1.0/eta, Kr, Kt,dumy1,dumy2);
		// Blinn/Phong highlight with Fresnel attenuation		
		colorValue += specularColor * Kr * pow ( max ( 0.0, dot(H,local_z)), 1.0/roughness );
	}
	
	
	gl_FragColor = colorValue;
}

