package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.apps.CityExplorer;
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

		Color3f diffuseColorValue = (Color3f) params[1];

		Vector3f light;
		if (Viewer.getMainViewer() != null)
			light = Viewer.getMainViewer().getLightPosition();
		else
			light = CityExplorer.getCityExplorer().getLightPosition();

		gl.glUniform4f(lightPosition, light.x, light.y, light.z, 1.0f);

		// Set the material properties
		gl.glUniform4f(diffuseColor, diffuseColorValue.x, diffuseColorValue.y,
				diffuseColorValue.z, 1.0f);
	}
}
