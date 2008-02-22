package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Vector3f;

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
public class ReflectionShader extends GLSLShader {
	protected int eyePosition, textureHandle;
	
	/**
	 * Default constructor
	 */
	public ReflectionShader() {
		super("reflection");
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		eyePosition = getNamedParameter(gl, "eyePosition");
		textureHandle = getNamedParameter(gl, "cubeMap");
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
		if (params.length < 2) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}

		// Unpack the parameters
		Vector3f eye = (Vector3f) params[0];
		gl.glUniform4f(eyePosition, eye.x, eye.y, eye.z, 1.0f);
		
		Texture t = ((Texture)params[1]);
		t.bindTexture(gl, 0);
		gl.glUniform1i(textureHandle, 0);
	}
}