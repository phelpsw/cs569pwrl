package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;

public class ShadowMap extends FrameBufferObject {
	public ShadowMap(String identifier, Camera lightCamera, int resolution) {
		super(identifier, DEPTH_TEXTURE_FBO, resolution);
	}

	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		
	}
}
