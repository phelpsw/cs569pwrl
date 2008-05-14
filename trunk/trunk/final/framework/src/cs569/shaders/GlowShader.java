package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.apps.TronRuntime;
import cs569.misc.GLSLErrorException;
import cs569.texture.Texture;

public class GlowShader extends GLSLShader {
	protected int lightPosition, eyePosition;
	protected int baseColorHandle;
	protected int glowColorHandle;
	protected int glowFactorHandle;
	private int glowFilterTextureHandle;
	
	protected GlowShader()
	{
		super("glow");
	}

	/**
	 * Default constructor
	 */
	protected GlowShader(String inName) {
		super(inName);
	}
	
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		lightPosition = getNamedParameter(gl, "lightPosition");
		baseColorHandle = getNamedParameter(gl, "baseColor");
		glowColorHandle = getNamedParameter(gl, "glowColor");
		glowFactorHandle = getNamedParameter(gl, "glowFactor");
		glowFilterTextureHandle = getNamedParameter(gl, "glowFilterTexture");
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length < 4) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}

		// Unpack the parameters
		Vector3f eye = (Vector3f) params[0];
		Color3f baseColorValue = (Color3f) params[1];
		Color3f glowColorValue = (Color3f) params[2];
		float glowFactor = ((Float) params[3]).floatValue();
		Texture glowFilterTexture = (Texture) params[4];
		
		gl.glUniform4f(eyePosition, eye.x, eye.y, eye.z, 1.0f);
		Vector3f light;

		light = TronRuntime.getMainViewer().getLightPosition();
		
		gl.glUniform4f(lightPosition, light.x, light.y, light.z, 1.0f);

		// Set the material properties
		gl.glUniform4f(baseColorHandle, baseColorValue.x, baseColorValue.y,
				baseColorValue.z, 1.0f);
		gl.glUniform4f(glowColorHandle, glowColorValue.x, glowColorValue.y,
				glowColorValue.z, 1.0f);
		
		gl.glUniform1f(glowFactorHandle, glowFactor);
		
		glowFilterTexture.bindTexture(gl, 0);
		gl.glUniform1i(glowFilterTextureHandle, 0);
	}

}
