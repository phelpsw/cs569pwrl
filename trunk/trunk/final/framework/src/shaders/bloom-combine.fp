#version 110

/* Uniform inputs */
uniform sampler2D sceneTexture;
uniform sampler2D bloomTexture;
uniform float bloomScale;

void main() {

	gl_FragColor =   texture2D(sceneTexture, gl_TexCoord[0].xy) +
		bloomScale * texture2D(bloomTexture, gl_TexCoord[0].xy);
}