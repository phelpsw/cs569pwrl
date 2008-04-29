package cs569.shaders;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;
import cs569.texture.Texture;


/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class TexturedPhongShader extends PhongShader {
	private int diffuseMapHandle;
	
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
		super.retrieveGLSLParams(gl);
		diffuseMapHandle = getNamedParameter(gl, "diffuseMap");
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
		super.setGLSLParams(gl, params);
		Texture diffuseMap = (Texture) params[4];
		diffuseMap.bindTexture(gl, 0);
		gl.glUniform1i(diffuseMapHandle, 0);
	}
}