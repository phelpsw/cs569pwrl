package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Vector3f;

import cs569.apps.Viewer;
import cs569.misc.GLSLErrorException;
import cs569.texture.Texture;

public class BloomShader extends GLSLShader {
	private int bloomTextureHandle;
	private int originalTextureHandle;
	private int modeHandle;
	private int w_texelHandle, h_texelHandle;
	private int exposureHandle;
	private float exp;
	/**
	 * Default constructor
	 */
	public BloomShader() {
		super("bloom");
		exp = 1.0f;
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		bloomTextureHandle = getNamedParameter(gl, "bloomTexture");
		originalTextureHandle = getNamedParameter(gl, "origBloomTexture");
		modeHandle = getNamedParameter(gl, "mode");
		w_texelHandle = getNamedParameter(gl, "w_texel");
		h_texelHandle = getNamedParameter(gl, "h_texel");
		exposureHandle = getNamedParameter(gl, "exposure");
	}
	
	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length < 2) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}
		
		float mode = ((Float) params[0]).floatValue();
		gl.glUniform1f(modeHandle, mode);
		
		float wtexel = ((Float) params[1]).floatValue();
		gl.glUniform1f(w_texelHandle, wtexel);
		
		float htexel = ((Float) params[2]).floatValue();
		gl.glUniform1f(h_texelHandle, htexel);
		
		Texture bloomMap = (Texture) params[3];
		bloomMap.bindTexture(gl, 0);
		gl.glUniform1i(bloomTextureHandle, 0);
		
		Texture original_bloomMap = (Texture) params[4];
		original_bloomMap.bindTexture(gl, 1);
		gl.glUniform1i(originalTextureHandle, 1);
		
		gl.glUniform1f(exposureHandle, exp);
	}
	
	public void incExp()
	{
		if(exp < 2.0f)
			exp+=0.05;
	}
	
	public void decExp()
	{
		if(exp > 1.0f)
			exp-=0.05;
	}
}
