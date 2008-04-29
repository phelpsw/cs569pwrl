package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;

public class ToneMappingShader extends GLSLShader {

	/** The GLSL program parameter handles. */
	protected int toneMapScale;
	protected int hdrTexture;

	/**
	 * Default constructor
	 */
	 
	protected ToneMappingShader() {
		super("tone-map");
	}

	/**
	 * Default constructor
	 */
	protected ToneMappingShader(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.shaders.GLSLShader#retrieveGLSLParams()
	 */
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		toneMapScale = getNamedParameter(gl, "toneMapScale");
		hdrTexture = getNamedParameter(gl, "hdrTexture");
	}

	/**
	 * Expects tone map scale and texture unit
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
		float toneMapScaleVal = (Float) params[0];
		int hdrTextureUnit = (Integer) params[1];

		// Set the material properties
		gl.glUniform1f(toneMapScale, toneMapScaleVal);
		gl.glUniform1i(hdrTexture, hdrTextureUnit);
	}

}
