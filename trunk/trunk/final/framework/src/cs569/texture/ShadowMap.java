package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.object.HierarchicalObject;

public class ShadowMap extends FrameBufferObject {
	protected Camera lightCamera;

	public ShadowMap(String identifier, Camera lightCamera, int width, int height) {
		super(identifier, DEPTH_TEXTURE_FBO, width, height);
		this.lightCamera = lightCamera;
	}

	public Camera getLightCamera() {
		return lightCamera;
	}

	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		lightCamera.updateMatrices();

		/* Render from the light's perspective */
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMultMatrixf(GLUtils.fromMatrix4f(lightCamera.getProjectionMatrix()), 0);
		gl.glMultMatrixf(GLUtils.fromMatrix4f(lightCamera.getViewMatrix()), 0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		/* Push the Z coordinates back just a little to avoid Z-fighting */
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(4.0f, 8.0f);
		object.glRender(gl, glu, lightCamera.getEye());
		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
	}
}
