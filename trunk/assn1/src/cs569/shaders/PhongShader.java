package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

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
public class PhongShader extends GLSLShader {
	
	/** The GLSL program parameter handles. */
	protected int lightPosition;
	protected int eyePosition;
	protected int diffuseColor;
	protected int specularColor;
	protected int exponent;
	
	
	/**
	 * Default constructor
	 */
	protected PhongShader() {
		super("phong");
	}

	/**
	 * Default constructor
	 */
	protected PhongShader(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.shaders.GLSLShader#retrieveGLSLParams()
	 */
	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		lightPosition = getNamedParameter(gl, "lightPosition");
		diffuseColor = getNamedParameter(gl, "diffuseColor");
		eyePosition = getNamedParameter(gl, "eyePosition");		
		specularColor = getNamedParameter(gl, "specularColor");
		exponent = getNamedParameter(gl, "exponent");
		
	}

	/**
	 * Expects eye, and diffuse color
	 * 
	 * @see cs569.shaders.GLSLShader#setGLSLParams(java.lang.Object[])
	 */
	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length != 4) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}		
		//params: eye, diffuseColor, specularColor, P
		

		// Unpack the parameters
		Vector3f eye = (Vector3f) params[0];
		Color3f diffuseColorValue = (Color3f) params[1];
		Color3f specularColorValue = (Color3f) params[2];
		float P = ((Float) params[3]).floatValue();

		gl.glUniform4f(eyePosition, eye.x, eye.y, eye.z, 1.0f);
		gl.glUniform4f(lightPosition, (float) Viewer.LIGHT_POSITION.x,
				(float) Viewer.LIGHT_POSITION.y,
				(float) Viewer.LIGHT_POSITION.z, 1.0f);

		// Set the material properties
		gl.glUniform4f(diffuseColor, diffuseColorValue.x, diffuseColorValue.y,
				diffuseColorValue.z, 1.0f);
		
		// Set the material properties
		gl.glUniform4f(specularColor, specularColorValue.x, specularColorValue.y,
				specularColorValue.z, 1.0f);
		
		gl.glUniform1f(exponent, P);
		
		

	}
}
