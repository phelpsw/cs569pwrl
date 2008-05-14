package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.WritingUtils;
import cs569.shaders.GlowShader;

public class Glow extends ShaderMaterial {
	
	// material properties
	protected final Color3f diffuseColor = new Color3f();

	public Glow() {
		super(GlowShader.class);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
	}
	
	public Glow(Color3f color) {
		super(GlowShader.class);
		diffuseColor.set(color);
	}
	
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor);
	}

	@Override
	public Material copy() {
		return new Glow(diffuseColor);
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeColor(out, diffuseColor, "diffuseColor", indent);
	}
	
	/**
	 * @see CS468.Materials.Material#getApproximateDiffuseColor()
	 */
	@Override
	public void getApproximateDiffuseColor(Color3f outColor) {
		outColor.set(diffuseColor);
	}
	
	// *******************************
	// * Material Property Functions *
	// *******************************
	public Color3f getDiffuseColor() {
		return diffuseColor;
	}

	public void setDiffuseColor(Color3f color) {
		diffuseColor.set(color);
	}

}
