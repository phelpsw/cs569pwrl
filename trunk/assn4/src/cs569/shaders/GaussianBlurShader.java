package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;
import cs569.texture.Texture;

public class GaussianBlurShader extends GLSLShader {

	/** The GLSL program parameter handles. */
	protected int texture;
	protected int variance;
	protected int width;
	protected int axis;
	protected int textureSize;

	/**
	 * Default constructor
	 */
	 
	protected GaussianBlurShader() {
		super("gaussian-blur");
	}

	/**
	 * Default constructor
	 */
	protected GaussianBlurShader(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.shaders.GLSLShader#retrieveGLSLParams()
	 */
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		texture = getNamedParameter(gl, "sourceTexture");
		variance = getNamedParameter(gl, "variance");
		width = getNamedParameter(gl, "width");
		axis = getNamedParameter(gl, "axis");
		textureSize = getNamedParameter(gl, "textureSize");
	}

	/**
	 * Expects luminance cutoff value, texture unit, HDR texture width, and HDR texture height
	 * 
	 * @see cs569.shaders.GLSLShader#setGLSLParams(java.lang.Object[])
	 */
	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length != 3) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}

		// Unpack the parameters
		Texture textureVal = (Texture) params[0];
		float varianceVal = (Float) params[1];
		int axisVal = (Integer) params[2];
		
		// Set the material properties
		textureVal.bindTexture(gl, 0);
		gl.glUniform1i(texture, 0);
		gl.glUniform1f(variance, varianceVal);
		gl.glUniform1i(axis, axisVal);
		
		if (axis == 0) gl.glUniform1f(textureSize, textureVal.getWidth());
		else           gl.glUniform1f(textureSize, textureVal.getHeight());
		
		// Don't go out past 2.5*std
		double maxRadius = 3 * Math.sqrt(varianceVal);
		int widthVal = (int)Math.ceil(maxRadius);
		gl.glUniform1i(width, widthVal);
	}
}
