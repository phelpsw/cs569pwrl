#version 110

/* Uniform inputs */
uniform sampler2D sourceTexture;
uniform float textureSize;

/* Width of the filter kernel */
uniform float variance;
uniform int width;

/* x-axis is 0, y-axis is non-zero */
uniform int axis;

void main() {

	float dx = 1.0 / textureSize;
	
	float mu = textureSize * gl_TexCoord[0].x;
	if (axis != 0)
		mu = textureSize * gl_TexCoord[0].y;
	
	float x = mu - float(width);
	
	float sumweight = 0.0;
	vec4 sumgauss = vec4(0.0, 0.0, 0.0, 0.0);
	
	for(int i = 0; i < 2*width+1; i++)
	{
		float gauss = x - mu;
		gauss = gauss*gauss;
		gauss = exp(-gauss / (2.0 * variance));
		
		vec4 sample;
		if (axis == 0)
			sample = texture2D(sourceTexture, vec2(dx*x, gl_TexCoord[0].y));
		else
			sample = texture2D(sourceTexture, vec2(gl_TexCoord[0].x, dx*x));
			
		sumgauss += gauss * sample;
		sumweight += gauss;
		
		x += 1.0;
	}
	
	gl_FragColor = sumgauss/sumweight;
}