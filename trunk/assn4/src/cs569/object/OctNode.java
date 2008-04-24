package cs569.object;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import cs569.misc.BoundingBox;

public class OctNode {

	private OctNode parent;
	private OctNode[] child = new OctNode[8];
	private ArrayList<MeshObject> obj = null;
	private float width;
	private Vector3f min = null;
	
	public OctNode(OctNode parent, Vector3f minPoint, float width)
	{
		this.parent = parent;
		min = new Vector3f(minPoint);
		this.width = width;		
	}
	
	// returns 1 if meshobject fits entirely inside this node
	// and thus was inserted
	public int insert(MeshObject o)
	{
		if (fitsEntirelyInNode(o.getBoundingBox()) == false)
		  return 0;
		
		// bounding box fully fits in this node
		if (child[0] == null)	
			initializeChildren();
	
		int countInside = 0;
		for (int i=0; i<child.length; i++)
		{
			countInside += child[i].insert(o);
		}
		
		// if meshobject didn't fit entirely in any smaller nodes
		if (countInside == 0)
		{
			if (obj == null)
				obj = new ArrayList<MeshObject>();
			
			obj.add(o);
		}
		
		return 1;
	}
	
	/**
	 * Initialize the 8 children of the cube as follows:
	 * 
	 * Back 4 children | Front 4 children
	 *    2  3               6  7 
	 *    0  1               4  5
	 * 
	 */
	private void initializeChildren()
	{
		float halfWidth = width/2;
		Vector3f tmp = new Vector3f();		
		
		//Back of cube
		tmp.set(min);
		child[0] = new OctNode(parent, tmp, halfWidth);
		
		tmp.x += halfWidth; 
		child[1] = new OctNode(parent, tmp, halfWidth);
		
		tmp.set(min);
		tmp.y += halfWidth;
		child[2] = new OctNode(parent, tmp, halfWidth);
		
		tmp.x += halfWidth; 
		child[3] = new OctNode(parent, tmp, halfWidth);
		
		
		// Front of cube
		tmp.set(min);
		tmp.z += halfWidth;
		child[4] = new OctNode(parent, tmp, halfWidth);
		
		tmp.x += halfWidth; 
		child[5] = new OctNode(parent, tmp, halfWidth);
		
		tmp.set(min);
		tmp.z += halfWidth;
		tmp.y += halfWidth;
		child[6] = new OctNode(parent, tmp, halfWidth);
		
		tmp.x += halfWidth; 
		child[7] = new OctNode(parent, tmp, halfWidth);

	}
	
	public int renderNode()
	{
		return 0;
	}
	
	private boolean fitsEntirelyInNode(BoundingBox b)
	{
		Vector3f boxMin = b.getMinPoint();
		Vector3f boxMax = b.getMaxPoint();
		
		Vector3f selfMax = new Vector3f(min);
		selfMax.x +=width; selfMax.y +=width; selfMax.z +=width; 
		if (min.x <= boxMin.x && min.y <= boxMin.y && min.z <= boxMin.z)
		{
			if (selfMax.x > boxMax.x && selfMax.y > boxMax.y && selfMax.z > boxMax.z)
			{
				return true;
			}
		}		
			
		return false;		
	}
	
}
