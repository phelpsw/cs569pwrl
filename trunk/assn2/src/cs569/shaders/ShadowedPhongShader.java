package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;

import cs569.apps.Viewer;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.texture.Texture;

/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class ShadowedPhongShader extends PhongShader {
	/** The GLSL program parameter handles. */
	//protected int lightPosition, eyePosition;
	//protected int diffuseColor, specularColor;
	//protected int exponent;
	protected int shadowHandle, cameraToLightT;

	
	/**
	 * Default constructor
	 */
	public ShadowedPhongShader() {
		super("shadowed-phong");
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		lightPosition = getNamedParameter(gl, "lightPosition");
		diffuseColor = getNamedParameter(gl, "diffuseColor");
		specularColor = getNamedParameter(gl, "specularColor");
		eyePosition = getNamedParameter(gl, "eyePosition");
		exponent = getNamedParameter(gl, "exponent");
		shadowHandle = getNamedParameter(gl, "shMap");
		cameraToLightT = getNamedParameter(gl, "cameraToLightT");
	}
	
	@Override
	public void setGLSLParams(GL gl, Object... params) {
		super.setGLSLParams(gl, params);
		
		if (params.length < 5) {
			throw new Error(this.getClass().getName()
					+ ": Invalid number of parameters.");
		}
		

		// Unpack the parameters
		Vector3f eye = (Vector3f) params[0];
		Color3f diffuseColorValue = (Color3f) params[1];
		Color3f specularColorValue = (Color3f) params[2];
		float exponentValue = ((Float) params[3]).floatValue();

		Texture t = ((Texture)params[4]);
		t.bindTexture(gl, 0);
		gl.glUniform1i(shadowHandle, 0);
		
	    gl.glUniform4f(eyePosition, eye.x, eye.y, eye.z, 1.0f);
		Vector3f light = Viewer.getMainViewer().getLightPosition();
		gl.glUniform4f(lightPosition, light.x, light.y, light.z, 1.0f);

		gl.glUniform1f(exponent, exponentValue);

		// Set the material properties
		gl.glUniform4f(diffuseColor, diffuseColorValue.x, diffuseColorValue.y,
				diffuseColorValue.z, 1.0f);
		gl.glUniform4f(specularColor, specularColorValue.x, specularColorValue.y,
				specularColorValue.z, 1.0f);
		
		// params: location, count (num of elements), transpose boolean, array, offset
		Matrix4f cam2Light = new Matrix4f();
		cam2Light.setIdentity();
				
		Viewer v = Viewer.getMainViewer();
		if (v.getCurrentCamera().equals(v.getViewCamera()))
		{
		  cam2Light.mul(cam2Light, v.getLightCamera().getProjectionMatrix());
		  cam2Light.mul(cam2Light, v.getLightCamera().getViewMatrix());
		  cam2Light.mul(cam2Light, v.getViewCamera().getInverseViewMatrix()); // now in world coord
		  cam2Light.mul(cam2Light, v.getViewCamera().getInverseProjectionMatrix());
		}
					

		gl.glUniformMatrix4fv(cameraToLightT, 1, false, GLUtils.fromMatrix4f(cam2Light), 0);
		
	}
}