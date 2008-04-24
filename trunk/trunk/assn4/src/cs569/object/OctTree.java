package cs569.object;

import javax.vecmath.Vector3f;

public class OctTree {

	private OctNode root;
	
	public OctTree(Vector3f minPoint, float sceneWidth)
	{
		root = new OctNode(null, minPoint, sceneWidth);
	}
	
	public boolean insert(MeshObject o)
	{
		root.insert(o);
		return true;
	}
	
	public void renderTree()
	{
		root.renderNode();
	}
	
}
