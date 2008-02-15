package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;


/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class TexturedPhongShader extends PhongShader {
	/**
	 * Default constructor
	 */
	public TexturedPhongShader() {
		super("textured-phong");
	}

	/**
	 * Default constructor
	 */
	protected TexturedPhongShader(String identifier) {
		super(identifier);
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
//		super.retrieveGLSLParams(gl);
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
//		super.setGLSLParams(gl, params);
	}
}