package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;
import cs569.texture.Texture;

public class BrightPassShader extends GLSLShader {

	/** The GLSL program parameter handles. */
	protected int cutoffLuminance;
	protected int hdrTexture;
	protected int textureWidth;
	protected int textureHeight;

	/**
	 * Default constructor
	 */
	 
	protected BrightPassShader() {
		super("bright-pass");
	}

	/**
	 * Default constructor
	 */
	protected BrightPassShader(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.shaders.GLSLShader#retrieveGLSLParams()
	 */
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		cutoffLuminance = getNamedParameter(gl, "cutoffLuminance");
		hdrTexture = getNamedParameter(gl, "hdrTexture");
		textureWidth = getNamedParameter(gl, "textureWidth");
		textureHeight = getNamedParameter(gl, "textureHeight");
	}

	/**
	 * Expects luminance cutoff value, texture unit, HDR texture width, and HDR texture height
	 * 
	 * @see cs569.shaders.GLSLShader#setGLSLParams(java.lang.Object[])
	 */
	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length != 2) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}

		// Unpack the parameters
		Texture input = (Texture) params[0];
		float cutoffLuminanceVal = (Float) params[1];

		// Set the material properties
		input.bindTexture(gl, 0);
		gl.glUniform1i(hdrTexture, 0);
		gl.glUniform1f(cutoffLuminance, cutoffLuminanceVal);
		gl.glUniform1f(textureWidth, (float)input.getWidth());
		gl.glUniform1f(textureHeight, (float)input.getHeight());
	}

}
