package cs569.material;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.shaders.ShadowedGlowShader;
import cs569.texture.Texture;

public class ShadowedGlow extends Glow {
	protected Texture shadowTexture;
	
	public ShadowedGlow() {
		super(ShadowedGlowShader.class);
		shadowTexture = Texture.getTexture("Shadow map");
	}

	public ShadowedGlow(Color3f baseColor, Color3f glowColor, float glowFactor, Texture glowFilterTexture, Texture shadowTexture) {
		super(ShadowedGlowShader.class);
		this.baseColor.set(baseColor);
		this.glowColor.set(glowColor);
		this.glowFactor = glowFactor;
		this.glowFilterTexture = glowFilterTexture;
		this.shadowTexture = shadowTexture;
	}
	
	public void setShadowMap(Texture texture) {
		this.shadowTexture = texture;
	}
	
	public Texture getShadowMap() {
		return shadowTexture;
	}
	
	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, baseColor, glowColor, glowFactor, glowFilterTexture, shadowTexture);
	}	

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new ShadowedGlow(baseColor, glowColor, glowFactor, glowFilterTexture, shadowTexture);
	}
	
}
