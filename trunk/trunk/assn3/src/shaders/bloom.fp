#version 110

/* Uniform inputs */
uniform sampler2D bloomTexture;
uniform sampler2D origBloomTexture;
uniform float mode;
uniform float w_texel;
uniform float h_texel;
uniform float exposure;

/* Inputs <- Vertex program */

void main() {
	vec4 colorValue=vec4(0.0);
	
	if(mode == 0.0)
	{
		colorValue += texture2D(bloomTexture, gl_TexCoord[0].xy);
		float L = colorValue.r + colorValue.g + colorValue.b;
		if(L < 1.0)
		{
			gl_FragColor = vec4(0.0);
		} else {
			gl_FragColor = colorValue;
		}
	} else if (mode == 1.0) {
		vec4 basecolor = texture2D(bloomTexture, gl_TexCoord[0].xy);
		float L = basecolor.r + basecolor.g + basecolor.b;
		if(L > 1.0)
		{
			colorValue = (1.0/16.0) * basecolor;
			colorValue += (4.0/16.0) * texture2D(origBloomTexture, (gl_TexCoord[0].xy - vec2(w_texel,0.0)));
			colorValue += (6.0/16.0) * texture2D(origBloomTexture, (gl_TexCoord[0].xy - vec2(2.0*w_texel,0.0)));
			colorValue += (4.0/16.0) * texture2D(origBloomTexture, (gl_TexCoord[0].xy + vec2(w_texel,0.0)));
			colorValue += (1.0/16.0) * texture2D(origBloomTexture, (gl_TexCoord[0].xy + vec2(2.0*w_texel,0.0)));
		} else {
			colorValue = vec4(0.0,0.0,0.0,0.0);
		}
		gl_FragColor = colorValue;
	} else if (mode == 2.0) {
		vec4 basecolor = texture2D(bloomTexture, gl_TexCoord[0].xy);
		float L = basecolor.r + basecolor.g + basecolor.b;
		if(L > 1.0)
		{
			colorValue = (1.0/16.0) * basecolor;
			colorValue += (4.0/16.0) * texture2D(bloomTexture, (gl_TexCoord[0].xy - vec2(0.0,h_texel)));
			colorValue += (6.0/16.0) * texture2D(bloomTexture, (gl_TexCoord[0].xy - vec2(0.0,2.0*h_texel)));
			colorValue += (4.0/16.0) * texture2D(bloomTexture, (gl_TexCoord[0].xy + vec2(0.0,h_texel)));
			colorValue += (1.0/16.0) * texture2D(bloomTexture, (gl_TexCoord[0].xy + vec2(0.0,2.0*h_texel)));
			colorValue *= exposure; // exposure amt
		} else {
			colorValue = vec4(0.0,0.0,0.0,0.0);
		}
		gl_FragColor = colorValue;
	} else if (mode == 3.0) {
		vec4 basecolor = texture2D(bloomTexture, gl_TexCoord[0].xy);
		float L = basecolor.r + basecolor.g + basecolor.b;
		if(L > 1.0)
		{
			gl_FragColor = texture2D(bloomTexture, gl_TexCoord[0].xy);
		} else {
			gl_FragColor = texture2D(origBloomTexture, gl_TexCoord[0].xy);
		}
	} else {
		gl_FragColor = vec4(0,0,1,0); // blue screen of death!! (this should never occur)
	}
}