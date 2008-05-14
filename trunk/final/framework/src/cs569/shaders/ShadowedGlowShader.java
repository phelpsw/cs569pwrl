package cs569.shaders;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;

import cs569.apps.TronRuntime;
import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.texture.ShadowMap;

public class ShadowedGlowShader extends GlowShader {
	private int shadowTextureHandle;
	private int eyeClipToLightClipHandle;
	private Matrix4f eyeClipToLightClip;
	
	/**
	 * Default constructor
	 */
	public ShadowedGlowShader() {
		super("shadowed-glow");
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

		Camera viewCamera = TronRuntime.getMainViewer().getCurrentCamera();
		ShadowMap shadowMap = (ShadowMap) params[5];

		Camera lightCamera = shadowMap.getLightCamera();
		eyeClipToLightClip.mul(lightCamera.getProjectionMatrix(), lightCamera.getViewMatrix());
		eyeClipToLightClip.mul(eyeClipToLightClip, viewCamera.getInverseViewMatrix());
		eyeClipToLightClip.mul(eyeClipToLightClip, viewCamera.getInverseProjectionMatrix());
		float[] data = GLUtils.fromMatrix4f(eyeClipToLightClip);
		gl.glUniformMatrix4fv(eyeClipToLightClipHandle, 1, false, data, 0);

		shadowMap.bindTexture(gl, 1);
		gl.glUniform1i(shadowTextureHandle, 1);
	}
}
