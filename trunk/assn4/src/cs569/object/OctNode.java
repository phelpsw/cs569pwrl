package cs569.object;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

public class OctNode {

	private OctNode parent;
	private OctNode[] child = new OctNode[8];
	private ArrayList obj = null;
	private float width;
	private Vector3f min = null;
	
	public OctNode(OctNode parent, Vector3f minPoint, float width)
	{
		this.parent = parent;
		min = new Vector3f(minPoint);
		this.width = width;		
	}
	
	public int insert(MeshObject o)
	{
		return 0;
	}
	
	
	public int renderNode()
	{
		return 0;
	}
}
