package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;
import cs569.texture.Texture;

public class BloomCombineShader extends GLSLShader {

	/** The GLSL program parameter handles. */
	protected int sceneTexture;
	protected int bloomTexture;
	protected int bloomScale;

	/**
	 * Default constructor
	 */
	 
	protected BloomCombineShader() {
		super("bloom-combine");
	}

	/**
	 * Default constructor
	 */
	protected BloomCombineShader(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.shaders.GLSLShader#retrieveGLSLParams()
	 */
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		sceneTexture = getNamedParameter(gl, "sceneTexture");
		bloomTexture = getNamedParameter(gl, "bloomTexture");
		bloomScale = getNamedParameter(gl, "bloomScale");
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
		Texture sceneTextureVal = (Texture) params[0];
		Texture bloomTexture1Val = (Texture) params[1];
		float bloomScale1Val = (Float) params[2];
		
		// Set the material properties
		sceneTextureVal.bindTexture(gl, 0);
		bloomTexture1Val.bindTexture(gl, 1);
		
		gl.glUniform1i(sceneTexture, 0);
		gl.glUniform1i(bloomTexture, 1);
		gl.glUniform1f(bloomScale, bloomScale1Val);
	}
}
