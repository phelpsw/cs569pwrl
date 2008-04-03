package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import javax.vecmath.Color3f;

import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.object.HierarchicalObject;

public class BloomMap extends FrameBufferObject {
	protected Camera lightCamera;

	public BloomMap(String identifier, Camera lightCamera, int width, int height) {
		super(identifier, HDR_TEXTURE_FBO, width, height);
		this.lightCamera = lightCamera;
	}

	public Camera getLightCamera() {
		return lightCamera;
	}
	
	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		lightCamera.updateMatrices();
		
		/* Render from the light's perspective*/
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMultMatrixf(GLUtils.fromMatrix4f(lightCamera.getProjectionMatrix()), 0);
		gl.glMultMatrixf(GLUtils.fromMatrix4f(lightCamera.getViewMatrix()), 0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		object.glRender(gl, glu, lightCamera.getEye());
	}

}
