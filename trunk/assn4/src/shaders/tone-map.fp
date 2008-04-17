#version 110

/* Uniform inputs */
uniform float toneMapScale;
uniform sampler2D hdrTexture;

void main() {
	vec4 colorValue = texture2D(hdrTexture, gl_TexCoord[0].xy);
	colorValue.rgb *= toneMapScale;
	gl_FragColor = colorValue;
}