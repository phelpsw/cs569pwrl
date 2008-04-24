package cs569.object;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;

public class OctTree {

	private OctNode root;
	
	public OctTree(Vector3f minPoint, float sceneWidth)
	{
		root = new OctNode(null, minPoint, sceneWidth);
	}
	
	public boolean insert(MeshObject o)
	{
		if(root.insert(o) == 0)
			return false;
		return true;
	}
	
	public void renderTree(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		root.renderNode(gl, glu, eye);
	}
	
}
