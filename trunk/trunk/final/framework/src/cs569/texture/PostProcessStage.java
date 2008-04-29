package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.shaders.GLSLShader;

public class PostProcessStage extends FrameBufferObject {

	GLSLShader shader;
	Object[] shaderArgs;
	
	public PostProcessStage(String identifier, int fboType, int width, int height, GLSLShader shader, Object... shaderArgs) {
		super(identifier, fboType, width, height);
		this.shader = shader;
		this.shaderArgs = shaderArgs;
	}
	
	public void setShaderArg(int idx, Object arg) {
		shaderArgs[idx] = arg;
	}
	
	@Override
	public void renderImpl(GL gl, GLU glu, HierarchicalObject object)
			throws GLSLErrorException {
		
		shader.bindShader(gl);
		shader.setGLSLParams(gl, shaderArgs);

		/* Set up an orthographic transformation */
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, 1, 0, 1, -1, 1);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		/* Draw a texture-mapped quad */
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(0, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(1, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(1, 1);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(0, 1);
		gl.glEnd();
	}

}
