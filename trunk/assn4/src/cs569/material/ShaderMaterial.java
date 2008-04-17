/*
 * Created on Jan 26, 2007
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package cs569.material;

import javax.media.opengl.GL;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;
import cs569.shaders.GLSLShader;

/**
 * Base class for materials that use GLSLShaders.
 * 
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public abstract class ShaderMaterial extends Material {
	/** The shader for this material */
	protected final GLSLShader shader;

	/**
	 * Set the shader for this material
	 * 
	 * @param inShaderClass
	 */
	protected ShaderMaterial(Class<? extends GLSLShader> inShaderClass) {
		shader = GLSLShader.getShader(inShaderClass);
	}

	/**
	 * @see cs569.material.Material#glSetMaterial(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	public final void glSetMaterial(GL gl, Vector3f eye)
			throws GLSLErrorException {
		shader.bindShader(gl);
		configureShader(gl, eye);
	}

	/**
	 * Configure the shader to prepare for rendering.
	 * 
	 * @param gl
	 * @param eye
	 */
	protected abstract void configureShader(GL gl, Vector3f eye);
}
