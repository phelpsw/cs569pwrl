package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;

/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class AnisotropicWardShader extends GLSLShader {
	/**
	 * Default constructor
	 */
	protected AnisotropicWardShader() {
		super("anisotropic-ward");
	}

	/**
	 * Default constructor
	 */
	protected AnisotropicWardShader(String inName) {
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
