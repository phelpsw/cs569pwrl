package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cs569.apps.TronRuntime;
import cs569.apps.Viewer;
import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.object.HierarchicalObject;
import cs569.shaders.GLSLShader;
import cs569.shaders.ToneMappingShader;

public class HDRSceneRenderer extends FrameBufferObject {
	
	protected Viewer viewer;
	protected TronRuntime tronRuntime;
	protected ToneMappingShader toneMapper;
	
	public boolean enabled;
	
	public HDRSceneRenderer(String identifier, Viewer viewer, int width, int height) {
		super(identifier, HDR_TEXTURE_FBO, width, height);
		toneMapper = (ToneMappingShader)GLSLShader.getShader(ToneMappingShader.class);
		this.viewer = viewer;
		this.tronRuntime = null;
	}
	
	public HDRSceneRenderer(String identifier, TronRuntime tronRuntime, int width, int height) {
		super(identifier, HDR_TEXTURE_FBO, width, height);
		toneMapper = (ToneMappingShader)GLSLShader.getShader(ToneMappingShader.class);
		this.viewer = null;
		this.tronRuntime = tronRuntime;
	}
	
	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		Camera camera;
		if(viewer == null)
			camera = tronRuntime.getCurrentCamera();
		else
			camera = viewer.getCurrentCamera();
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMultMatrixf(GLUtils.fromMatrix4f(camera.getProjectionMatrix()), 0);
		gl.glMultMatrixf(GLUtils.fromMatrix4f(camera.getViewMatrix()), 0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		object.glRender(gl, glu, camera.getEye());
		if(viewer == null) // tron activated!
		{
			if (tronRuntime.getParticleSystem() != null)
				tronRuntime.getParticleSystem().glRender(gl, glu, camera.getEye());
		} else { // in viewer mode
			viewer.getRotationGizmo().glRender(gl, glu, camera.getEye());
			if (viewer.getParticleSystem() != null)
				viewer.getParticleSystem().glRender(gl, glu, camera.getEye());
		}
	}
	
	public void doToneMapping(GL gl, Texture texture)
	{		
		toneMapper.bindShader(gl);
		toneMapper.setGLSLParams(gl, 1.0f, 0);
		
		texture.blit(gl);
	}

}
