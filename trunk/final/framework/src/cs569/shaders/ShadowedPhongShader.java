package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;

import cs569.apps.Viewer;
import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.texture.ShadowMap;

/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class ShadowedPhongShader extends PhongShader {
	private int shadowTextureHandle;
	private int eyeClipToLightClipHandle;
	private Matrix4f eyeClipToLightClip;

	/**
	 * Default constructor
	 */
	public ShadowedPhongShader() {
		super("shadowed-phong");
		eyeClipToLightClip = new Matrix4f();
	}

	@Override
	protected void retrieveGLSLParams(GL gl) throws GLSLErrorException {
		super.retrieveGLSLParams(gl);
		shadowTextureHandle = getNamedParameter(gl, "shadowTexture");
		eyeClipToLightClipHandle = getNamedParameter(gl, "eyeClipToLightClip");
	}
	
	@Override
	public void setGLSLParams(GL gl, Object... params) {
		super.setGLSLParams(gl, params);

		Camera viewCamera = Viewer.getMainViewer().getCurrentCamera();
		ShadowMap shadowMap = (ShadowMap) params[4];

		Camera lightCamera = shadowMap.getLightCamera();
		eyeClipToLightClip.mul(lightCamera.getProjectionMatrix(), lightCamera.getViewMatrix());
		eyeClipToLightClip.mul(eyeClipToLightClip, viewCamera.getInverseViewMatrix());
		eyeClipToLightClip.mul(eyeClipToLightClip, viewCamera.getInverseProjectionMatrix());
		float[] data = GLUtils.fromMatrix4f(eyeClipToLightClip);
		gl.glUniformMatrix4fv(eyeClipToLightClipHandle, 1, false, data, 0);

		shadowMap.bindTexture(gl, 0);
		gl.glUniform1i(shadowTextureHandle, 0);
	}
}