package cs569.object;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import cs569.camera.Camera;
import cs569.misc.BoundingBox;
import cs569.misc.GLSLErrorException;

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
	
		int inside = 0;
		for (int i=0; i<child.length; i++)
		{
			if (child[i].insert(o) > 0)
			{
				inside = 1;
				break;
			}
		}
		
		// if meshobject didn't fit entirely in any smaller nodes
		if (inside == 0)
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
	
	public void renderNode(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		if(nodeInFrustum())
		{
			renderSelfObjects(gl, glu, eye);
			renderChildren(gl, glu, eye);
		}
	}
	
	private void renderSelfObjects(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		if(obj != null)
		{
			for(int i=0; i<obj.size(); i++)
			{
				if(obj.get(i).boxInFrustum())
					obj.get(i).glRender(gl, glu, eye);
			}
		}
	}
	
	private void renderChildren(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		if(child[0] == null)
			return;
		for(int i=0; i<8;i++)
			child[i].renderNode(gl, glu, eye);
	}
	
	private boolean nodeInFrustum() 
	{
		Vector3f max = new Vector3f(min);
		max.x+=width;max.y+=width;max.z+=width;
		BoundingBox boundingBox = new BoundingBox();
		boundingBox.expandBy(min);
		boundingBox.expandBy(max);
		
		boolean result = true;
		//for each plane do ...
		for(int i=0; i < 6; i++) {

			// is the positive vertex outside?
			if (Camera.fPlane[i].distance(boundingBox.getVertexP(Camera.fPlane[i].getNormal())) < 0)
				return false;
			// is the negative vertex outside?	
			else if (Camera.fPlane[i].distance(boundingBox.getVertexN(Camera.fPlane[i].getNormal())) < 0)
				result =  true;
		}
		return result;
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
