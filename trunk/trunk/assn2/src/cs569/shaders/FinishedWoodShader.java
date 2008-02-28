package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.apps.Viewer;
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
	protected int lightPosition, specularColor, eyePosition;
	protected int etaHandle, roughnessHandle;
	protected int diffuseTexture, axisTexture, betaTexture, fiberTexture;
	
	/**
	 * Default constructor
	 */
	public FinishedWoodShader() {
		super("finished-wood");
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		lightPosition = getNamedParameter(gl, "lightPosition");		
		specularColor = getNamedParameter(gl, "specularColor");
		eyePosition = getNamedParameter(gl, "eyePosition");
		etaHandle = getNamedParameter(gl, "eta");
		roughnessHandle = getNamedParameter(gl, "roughness");
		diffuseTexture = getNamedParameter(gl, "diffuseTexture");
		axisTexture = getNamedParameter(gl, "axisTexture");
		betaTexture = getNamedParameter(gl, "betaTexture");
		fiberTexture = getNamedParameter(gl, "fiberTexture");
	}

	@Override
	public void setGLSLParams(GL gl, Object... params) {
	//shader.setGLSLParams(gl, eye, specularColor, eta, roughness, diffuseTexture, axisTexture, betaTexture, fiberTexture);
		
		if (params.length < 8) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}

		Vector3f light = Viewer.getMainViewer().getLightPosition();
		gl.glUniform4f(lightPosition, light.x, light.y, light.z, 1.0f);
		
		Vector3f eye = (Vector3f) params[0];
		gl.glUniform4f(eyePosition, eye.x, eye.y, eye.z, 1.0f);
		
		Color3f specularColorValue = (Color3f) params[1];
		gl.glUniform4f(specularColor, specularColorValue.x, specularColorValue.y,
				specularColorValue.z, 1.0f);
		
		float eta = ((Float) params[2]).floatValue();
		gl.glUniform1f(etaHandle, eta);
		
		float roughness = ((Float) params[3]).floatValue();
		gl.glUniform1f(roughnessHandle, roughness);
		
		Texture tDiffuse = ((Texture)params[4]);
		tDiffuse.bindTexture(gl, 0);
		gl.glUniform1i(diffuseTexture, 0);
		
		Texture tAxis = ((Texture)params[5]);
		tAxis.bindTexture(gl, 1);
		gl.glUniform1i(axisTexture, 1);
		
		Texture tBeta = ((Texture)params[6]);
		tBeta.bindTexture(gl, 2);
		gl.glUniform1i(betaTexture, 2);
		
		Texture tFiber = ((Texture)params[7]);
		tFiber.bindTexture(gl, 3);
		gl.glUniform1i(fiberTexture, 3);
		
		
	}
}