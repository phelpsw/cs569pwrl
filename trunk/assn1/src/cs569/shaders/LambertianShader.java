package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;

import cs569.apps.Viewer;
import cs569.misc.GLSLErrorException;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public class LambertianShader extends GLSLShader {
	/** The GLSL program parameter handles. */
	protected int lightPosition;
	protected int eyePosition;
	protected int diffuseColor;

	/**
	 * Default constructor
	 */
	protected LambertianShader() {
		super("lambertian");
	}

	/**
	 * Default constructor
	 */
	protected LambertianShader(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.shaders.GLSLShader#retrieveGLSLParams()
	 */
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		lightPosition = getNamedParameter(gl, "lightPosition");
		diffuseColor = getNamedParameter(gl, "diffuseColor");
//		eyePosition = getNamedParameter(gl, "eyePosition");
	}

	/**
	 * Expects eye, and diffuse color
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
//		Vector3f eye = (Vector3f) params[0];
		Color3f diffuseColorValue = (Color3f) params[1];

//		gl.glUniform4f(eyePosition, eye.x, eye.y, eye.z, 1.0f);
		gl.glUniform4f(lightPosition, (float) Viewer.LIGHT_POSITION.x,
				(float) Viewer.LIGHT_POSITION.y,
				(float) Viewer.LIGHT_POSITION.z, 1.0f);

		// Set the material properties
		gl.glUniform4f(diffuseColor, diffuseColorValue.x, diffuseColorValue.y,
				diffuseColorValue.z, 1.0f);
	}
}
