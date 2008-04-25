package cs569.misc;

import javax.vecmath.Vector3f;

public class BoundingBox {

	Vector3f minPoint = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	Vector3f maxPoint = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
	Vector3f tmpPoint = new Vector3f();
	
	private boolean initialized = false;
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
	public void expandBy(Vector3f point)
	{
		initialized = true;
		
		if (point.x > maxPoint.x)
			maxPoint.x = point.x;		
		if (point.y > maxPoint.y)
			maxPoint.y = point.y;		
		if (point.z > maxPoint.z)
			maxPoint.z = point.z;		
				
		if (point.x < minPoint.x)
			minPoint.x = point.x;		
		if (point.y < minPoint.y)
			minPoint.y = point.y;		
		if (point.z < minPoint.z)
			minPoint.z = point.z;		
		
	}
	
	public Vector3f getVertexP(Vector3f norm)
	{
		tmpPoint.set(minPoint);
		if(norm.x >= 0)
			tmpPoint.x = maxPoint.x;
		if(norm.y >= 0)
			tmpPoint.y = maxPoint.y;
		if(norm.z >= 0)
			tmpPoint.z = maxPoint.z;
		return tmpPoint;
	}
	
	public Vector3f getVertexN(Vector3f norm)
	{
		tmpPoint.set(maxPoint);
		if(norm.x >= 0)
			tmpPoint.x = minPoint.x;
		if(norm.y >= 0)
			tmpPoint.y = minPoint.y;
		if(norm.z >= 0)
			tmpPoint.z = minPoint.z;
		return tmpPoint;
	}
	
	
	
	public void expandBy(BoundingBox b)
	{
		expandBy(b.maxPoint);
		expandBy(b.minPoint);		
	}
	
	public Vector3f getMinPoint()
	{
		return minPoint;
	}
	
	public Vector3f getMaxPoint()
	{
		return maxPoint;
	}
	
	
	
	public Vector3f getVertex(int i)
	{
		if (i==0)
			return minPoint;
		else if (i==1)
			return maxPoint;
		else
			return null;
	}
	
	public String toString()
	{
	 return "(" + minPoint + ", " + maxPoint + ")";
	}
}
