package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.apps.TronRuntime;
import cs569.misc.GLSLErrorException;

public class GlowShader extends GLSLShader {
	protected int lightPosition;
	protected int eyePosition;
	protected int diffuseColor;
	
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
		diffuseColor = getNamedParameter(gl, "diffuseColor");

	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length != 2) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}

		Color3f diffuseColorValue = (Color3f) params[1];

		Vector3f light;
		light = TronRuntime.getMainViewer().getLightPosition();
			

		gl.glUniform4f(lightPosition, light.x, light.y, light.z, 1.0f);

		// Set the material properties
		gl.glUniform4f(diffuseColor, diffuseColorValue.x, diffuseColorValue.y,
				diffuseColorValue.z, 1.0f);
	}

}
