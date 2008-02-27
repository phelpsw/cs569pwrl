package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.object.HierarchicalObject;

import javax.vecmath.Vector3f;

public class ShadowMap extends FrameBufferObject {
	Camera lightCamera;
	public ShadowMap(String identifier, Camera lightCamera, int resolution) {
		super(identifier, DEPTH_TEXTURE_FBO, resolution);
		this.lightCamera = lightCamera;
	}

	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		
		System.out.println(lightCamera.getEye());
		Vector3f eye = new Vector3f();
		
		lightCamera.updateMatrices();
		eye.set(lightCamera.getEye());
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glMultMatrixf(GLUtils.fromMatrix4f(lightCamera.getProjectionMatrix()), 0);
		gl.glMultMatrixf(GLUtils.fromMatrix4f(lightCamera.getViewMatrix()), 0);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glUseProgram(0);

		object.glRender(gl, glu, eye);
		//gl.glRectf(0.0f, 0.0f, 0.75f, 0.75f);
	}
	
	
}
