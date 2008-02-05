package cs569.shaders;

import javax.media.opengl.GL;

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
	}

	/**
	 * Expects eye, and diffuse color
	 * 
	 * @see cs569.shaders.GLSLShader#setGLSLParams(java.lang.Object[])
	 */
	@Override
	public void setGLSLParams(GL gl, Object... params) {
	}
}
