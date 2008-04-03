package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import javax.vecmath.Color3f;
import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.object.HierarchicalObject;
import cs569.shaders.BloomShader;
import cs569.shaders.GLSLShader;

public class BloomMask extends FrameBufferObject {
	/** Brightness Mask Mode */
	public final static float BLOOM_BRIGHT_MASK = 0.0f;

	/** Horizontal Convolution */
	public final static float BLOOM_CONV_H = 1.0f;

	/** Vertical Convolution */
	public final static float BLOOM_CONV_V = 2.0f;
	
	/** Combination with original */
	public final static float BLOOM_COMBINE = 3.0f;
	
	protected Texture map;
	protected Texture original_map;
	protected float mode;
	
	public BloomMask(String identifier, Texture map, float mode, int width, int height) {
		super(identifier, HDR_TEXTURE_FBO, width, height);
		this.map = map;
		this.mode = mode;
		if(mode == BLOOM_BRIGHT_MASK)
			this.original_map = map;
		else
			throw new FrameBufferObjectException("Invalid BloomMask Mode - Invalid constructor choice");
	}
	
	public BloomMask(String identifier, Texture map, Texture original_map, float mode, int width, int height) {
		super(identifier, HDR_TEXTURE_FBO, width, height);
		this.map = map;
		this.mode = mode;
		if(mode != BLOOM_BRIGHT_MASK)
			this.original_map = original_map;
		else
			throw new FrameBufferObjectException("Invalid BloomMask Mode");
	}
	
	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object)
			throws GLSLErrorException {
		BloomShader bs = (BloomShader)GLSLShader.getShader(BloomShader.class);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		bs.bindShader(gl);
		bs.setGLSLParams(gl, mode, (1.0f/width), (1.0f/height), map, original_map);
		
		/* Draw a texture-mapped quad */
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(-1, -1, .5f);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(1, -1, .5f);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(1, 1, .5f);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(-1, 1, .5f);
		gl.glEnd();
		
	}

}
