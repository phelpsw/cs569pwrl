package cs569.object;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;

public class OctTree {

	private OctNode root;
	private float minx = Float.MAX_VALUE;
	private float miny = Float.MAX_VALUE;
	private float minz = Float.MAX_VALUE;
	
	private float maxx = Float.MIN_VALUE;
	private float maxy = Float.MIN_VALUE;
	private float maxz = Float.MIN_VALUE;
	
	
	public OctTree(Vector3f minPoint, float sceneWidth)
	{
		root = new OctNode(null, minPoint, sceneWidth);
	}
	
	public boolean insert(MeshObject o)
	{
		Vector3f p = o.boundingBox.getMinPoint();
		if (minx > p.x)
		  minx = p.x;
		if (miny > p.y)
		 miny = p.y;
		if (minz > p.z)
			 minz = p.z;
		
		p = o.boundingBox.getMaxPoint();
		if (maxx < p.x)
		  maxx = p.x;
		if (maxy < p.y)
		 maxy = p.y;
		if (maxz < p.z)
			 maxz = p.z;
		
		if(root.insert(o) == 0)
		{
			System.out.println("won't fit: BB=" + o.boundingBox + ", node :" + root);		
			
			return false;
		}
		return true;
	}
	
	public void printExtreme()
	{
		System.out.println("min: " + minx + ", " + miny + ", " + minz);
		System.out.println("max: " + maxx + ", " + maxy + ", " + maxz);		
	}
	
	public void renderTree(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		root.renderNode(gl, glu, eye);
	}
	
}
