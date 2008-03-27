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
	private int cubeMapHandle;
	private int eyePositionHandle;

	/**
	 * Default constructor
	 */
	public ReflectionShader() {
		super("reflection");
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		cubeMapHandle = getNamedParameter(gl, "cubeMap");
		eyePositionHandle = getNamedParameter(gl, "eyePosition");
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
		Vector3f eye = (Vector3f) params[0];
		Texture cubeMap = (Texture) params[1];
		gl.glUniform4f(eyePositionHandle, eye.x, eye.y, eye.z, 1.0f);
		cubeMap.bindTexture(gl, 0);
		gl.glUniform1i(cubeMapHandle, 0);
	}
}