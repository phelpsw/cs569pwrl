#version 110

/** 
 * Wood shader implementation based on
 * "Measuring and Modeling the Appearance of Finished Wood"
 * by Stephen R. Marschner, Stephen H. Westin, Adam Arbree, 
 * and Jonathan T. Moon. In Proceedings of SIGGRAPH 2005. 
 * Ported from RenderMan to GLSL by Wenzel Jakob
 */

/* Uniform inputs */
uniform sampler2D texDiffuse;
uniform sampler2D texFiber;
uniform sampler2D texAxis;
uniform sampler2D texBeta;
uniform float eta, roughness;
uniform vec4 specularColor;

/* Inputs <- Vertex program */
varying vec3 lightVector, eyeVector;

/* The function calculates the reflected and refracted directions
   in addition to the fresnel coefficients. Based on the paper
   "Derivation of Refraction Formulas" by Paul S. Heckbert */

void fresnel(in vec3 wi, in vec3 n, in float etaExt, in float etaInt,
	out float Fr, out float Ft, out vec3 Vr, out vec3 Vt) {

	float eta = etaExt / etaInt;
	float cosTheta1 = dot(wi, n);
	float cosTheta2 = sqrt(1.0 - eta*eta * 
					(1.0 - cosTheta1 * cosTheta1));
	Vr = wi - (2.0 * cosTheta1) * n;
	Vt = wi*eta + (cosTheta2 - eta * cosTheta1)*n;

	/* Recycle cosine values to calculate the fresnel coefficients */
	float Rs = (etaExt * cosTheta1 - etaInt * cosTheta2)
			 / (etaExt * cosTheta1 + etaInt * cosTheta2);
	float Rp = (etaInt * cosTheta1 - etaExt * cosTheta2)
			 / (etaInt * cosTheta1 + etaExt * cosTheta2);

	Fr = (Rs * Rs + Rp * Rp) / 2.0;

	/* Assuming a dielectric material */
	Ft = 1.0 - Fr;
}

const float sqrt2Pi = 2.5066283;

void main() {
	/* Interpolated directions need to be re-normalized */
	vec3 nEyeVector = normalize(eyeVector);
	vec3 nLightVector = normalize(lightVector);

	/* Texture fetch */
	vec4 tDiffuse = texture2D(texDiffuse, gl_TexCoord[0].xy);
	vec4 tFiber = texture2D(texFiber, gl_TexCoord[0].xy) * 15.0;
	vec3 tAxis = normalize(texture2D(texAxis, gl_TexCoord[0].xy).rgb - 0.5);
	float beta = texture2D(texBeta, gl_TexCoord[0].xy).r;

	vec4 fragColor = vec4(0.05);
	vec3 ssOutDir, ssInDir;
	float ssAttenOut, ssAtten;

	/* Calculate fresnel transmission values + directions */
	if (eta != 1.0) {
		float dummy1; vec3 dummy2;
		fresnel(nEyeVector, vec3(0,0,1), 1.0, eta, 
			dummy1, ssAtten, dummy2, ssOutDir);

		fresnel(nLightVector, vec3(0,0,1), 1.0, eta,
			dummy1, ssAttenOut, dummy2, ssInDir);
		ssInDir = -ssInDir;
	} else {
		ssOutDir = nEyeVector;
		ssAtten = 1.0;
		ssInDir = -nLightVector;
		ssAttenOut = 1.0;
	}

	/* Calculate inclination relative to the fiber axis */
	float thOutPrime = asin(dot(ssOutDir, tAxis));
	float thInPrime = asin(-dot(ssInDir, tAxis));
	float halfAngle = thOutPrime + thInPrime;
	float diffAngle = thOutPrime - thInPrime;

	/* Fraction of light transmitted through the sub-surface scattering path */
	float ssFactor = max(0.0, -ssInDir.z) * ssAtten * ssAttenOut;

	/* Calculate the reflection due to fibers */
	float gaussian = beta * exp(-pow(halfAngle/beta,2.0) / 2.0) / sqrt2Pi;
	float fiberFactor = gaussian / pow(cos(diffAngle/2.0), 2.0);

	if (nEyeVector.z > 0.0 && nLightVector.z > 0.0) {
		/* Diffuse term, attenuated by fresnel transmittance factors */
		fragColor += tDiffuse * ssFactor;
	
		/* Fiber highlight term */
		fragColor += fiberFactor * tFiber * ssFactor;

		/* Blinn-phong term */
		vec3 halfVector = normalize(eyeVector + lightVector);
		float Fr, dummy1; vec3 dummy2, dummy3;

		/* Most of this call will get optimized away */
		fresnel(nEyeVector, halfVector, 1.0, eta,
			Fr, dummy1, dummy2, dummy3);

		/* Cosine factor missing in RenderMan-shader? */
		fragColor += specularColor * (Fr * pow(max(0.0, halfVector.z),
			 1.0/roughness) * nLightVector.z); 
	}
	gl_FragColor = fragColor;
}
