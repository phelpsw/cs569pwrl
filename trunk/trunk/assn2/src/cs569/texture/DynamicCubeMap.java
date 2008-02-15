package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;

public class DynamicCubeMap extends FrameBufferObject {
	public DynamicCubeMap(String identifier, Vector3f eyePosition, int resolution) {
		super(identifier, CUBEMAP_TEXTURE_FBO, resolution);
	}

	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
	}
}