package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.object.HierarchicalObject;

public class DynamicCubeMap extends FrameBufferObject {
	protected static final Vector3f POSITIVE_X = new Vector3f(1.0f, 0.0f, 0.0f);
	protected static final Vector3f NEGATIVE_X = new Vector3f(-1.0f, 0.0f, 0.0f);
	protected static final Vector3f POSITIVE_Y = new Vector3f(0.0f, 1.0f, 0.0f);
	protected static final Vector3f NEGATIVE_Y = new Vector3f(0.0f, -1.0f, 0.0f);
	protected static final Vector3f POSITIVE_Z = new Vector3f(0.0f, 0.0f, 1.0f);
	protected static final Vector3f NEGATIVE_Z = new Vector3f(0.0f, 0.0f, -1.0f);
	protected Camera camera;
	protected Vector3f eyePosition;

	public DynamicCubeMap(String identifier, Vector3f eyePosition, int resolution) {
		super(identifier, CUBEMAP_TEXTURE_FBO, resolution, resolution);
		camera = new Camera(eyePosition, new Vector3f(0,0,0), 
					new Vector3f(0,1,0), 90.0f, 0.1f, 100.0f);
		this.eyePosition = eyePosition;
	}

	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);

		for (int i=0; i<6; i++) {
			int position = GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i;
			gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,
	                   position, textureID, 0);

			switch (position) {
			case GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X: 
				camera.setDirection(POSITIVE_X);
				camera.setUp(POSITIVE_Y);
				break;
			case GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X: 
				camera.setDirection(NEGATIVE_X);
				camera.setUp(POSITIVE_Y);
				break;
			case GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y: 
				camera.setDirection(NEGATIVE_Y);
				camera.setUp(POSITIVE_Z);
				break;
			case GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y: 
				camera.setDirection(POSITIVE_Y);
				camera.setUp(POSITIVE_Z);
				break;
			case GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z: 
				camera.setDirection(NEGATIVE_Z);
				camera.setUp(NEGATIVE_Y);
				break;
			case GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z: 
				camera.setDirection(POSITIVE_Z);
				camera.setUp(NEGATIVE_Y);
				break;
			default:
				camera.setDirection(NEGATIVE_Z);
				camera.setUp(POSITIVE_Y);
			};

			camera.updateMatrices();
	
			/* Render from the light's perspective */
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glMultMatrixf(GLUtils.fromMatrix4f(camera.getProjectionMatrix()), 0);
			gl.glMultMatrixf(GLUtils.fromMatrix4f(camera.getViewMatrix()), 0);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);

			object.glRender(gl, glu, camera.getEye());
		}

		gl.glDisable(GL.GL_CULL_FACE);
	}
}