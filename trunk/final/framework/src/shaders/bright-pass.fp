#version 110

/* Uniform inputs */
uniform float cutoffLuminance;
uniform sampler2D hdrTexture;
uniform float textureWidth;
uniform float textureHeight;

void main() {
	
	vec4 colorValue = texture2D(hdrTexture, gl_TexCoord[0].xy);
	
	gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
	if(0.299 * colorValue.r + 0.587*colorValue.g + 0.114*colorValue.b > cutoffLuminance)
		gl_FragColor = colorValue;
}