package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.WritingUtils;
import cs569.shaders.GLSLShader;
import cs569.shaders.GlowShader;
import cs569.texture.Texture;

public class Glow extends ShaderMaterial {
	
	// material properties
	protected Color3f baseColor = new Color3f();
	protected Color3f glowColor = new Color3f();
	protected float glowFactor;
	protected Texture glowFilterTexture;

	public Glow() {
		super(GlowShader.class);
		baseColor.set(1.0f, 1.0f, 1.0f);
		glowColor.set(1.0f, 1.0f, 1.0f);
		glowFactor = 1.0f;
		glowFilterTexture = Texture.getTexture("/textures/tron/glowpattern.png");
	}
	
	public Glow(Class<? extends GLSLShader> shaderClass) {
		super(shaderClass);
		baseColor.set(1.0f, 1.0f, 1.0f);
		glowColor.set(1.0f, 1.0f, 1.0f);
		glowFactor = 1.0f;
		glowFilterTexture = Texture.getTexture("/textures/tron/glowpattern.png");
	}
	
	public Glow(Color3f baseColor, Color3f glowColor, float glowFactor, Texture glowFilterTexture) {
		super(GlowShader.class);
		this.baseColor.set(baseColor);
		this.glowColor.set(glowColor);
		this.glowFactor = glowFactor;
		this.glowFilterTexture = glowFilterTexture;
	}
	
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, baseColor, glowColor, glowFactor, glowFilterTexture);
	}

	@Override
	public Material copy() {
		return new Glow(baseColor, glowColor, glowFactor, glowFilterTexture);
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeColor(out, baseColor, "diffuseColor", indent);
	}
	
	/**
	 * @see CS468.Materials.Material#getApproximateDiffuseColor()
	 */
	@Override
	public void getApproximateDiffuseColor(Color3f outColor) {
		outColor.set(baseColor);
	}
	
	// *******************************
	// * Material Property Functions *
	// *******************************
	public Color3f getBaseColor() {
		return baseColor;
	}

	public void setBaseColor(Color3f color) {
		baseColor.set(color);
	}

}
