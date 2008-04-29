package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.apps.TronRuntime;
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
public class FinishedWoodShader extends GLSLShader {
	protected int lightPositionHandle, eyePositionHandle;
	protected int specularColorHandle, etaHandle;
	protected int roughnessHandle, axisTextureHandle;
	protected int fiberTextureHandle, diffuseTextureHandle;
	protected int betaTextureHandle;

	/**
	 * Default constructor
	 */
	public FinishedWoodShader() {
		super("finished-wood");
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		lightPositionHandle = getNamedParameter(gl, "lightPosition");
		eyePositionHandle = getNamedParameter(gl, "eyePosition");
		specularColorHandle = getNamedParameter(gl, "specularColor");
		etaHandle = getNamedParameter(gl, "eta");
		roughnessHandle = getNamedParameter(gl, "roughness");
		diffuseTextureHandle = getNamedParameter(gl, "texDiffuse");
		fiberTextureHandle = getNamedParameter(gl, "texFiber");
		axisTextureHandle = getNamedParameter(gl, "texAxis");
		betaTextureHandle = getNamedParameter(gl, "texBeta");
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
		Vector3f eye = (Vector3f) params[0];
		Color3f specularColor = (Color3f) params[1];
		float eta = ((Float) params[2]).floatValue();
		float roughness = ((Float) params[3]).floatValue();
		Texture diffuseTexture = (Texture) params[4];
		Texture axisTexture = (Texture) params[5];
		Texture betaTexture = (Texture) params[6];
		Texture fiberTexture = (Texture) params[7];
		
		diffuseTexture.bindTexture(gl, 0);
		axisTexture.bindTexture(gl, 1);
		betaTexture.bindTexture(gl, 2);
		fiberTexture.bindTexture(gl, 3);
		
		gl.glUniform4f(eyePositionHandle, eye.x, eye.y, eye.z, 1.0f);
		Vector3f light = TronRuntime.getMainViewer().getLightPosition();
		gl.glUniform4f(lightPositionHandle, light.x, light.y, light.z, 1.0f);

		gl.glUniform1f(roughnessHandle, roughness);
		gl.glUniform1f(etaHandle, eta);
		gl.glUniform1i(diffuseTextureHandle, 0);
		gl.glUniform1i(axisTextureHandle, 1);
		gl.glUniform1i(betaTextureHandle, 2);
		gl.glUniform1i(fiberTextureHandle, 3);
		gl.glUniform4f(specularColorHandle, specularColor.x, specularColor.y,
				specularColor.z, 1.0f);
	}
}