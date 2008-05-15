package cs569.tron;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;

public class Wall extends HierarchicalObject {

	Vector2f start = new Vector2f();
	Vector2f end = new Vector2f();
	Vector2f dir = new Vector2f();
	static float height = 1;
	static float width = .05f;
	Material material;
	MeshObject box;
	
	static float offset = 5; 
	
	public Wall(Vector2f startPos, Vector2f direction)
	{
		start.set(startPos);
		end.set(startPos);		
		dir.set(direction);
		end.x = end.x - dir.x * offset;
		end.y = end.y - dir.y * offset;
		
		//System.out.println("New wall start: " + start + ", end: " + end + ", dir: " + dir);
		
		name = "WallTrail " + start.x + " " + start.y;
	
		box = PrimitiveFactory.makeBox("WallTrailBox_" + startPos.x + "_" + startPos.y);		
		box.setTranslate(startPos.x, 0.0f, startPos.y);
		box.collidable = true;

	}
	
	public void setCollidable(boolean c)
	{
		box.collidable = c;
	}
		
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException {
		//draw a box.  No super
		//TODO draw wall.  Use box primitive
		
	}

	public void setMaterial(Material m)
	{	
		box.setMaterial(m);
	}
	
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		// TODO Auto-generated method stub
	}
	
	Vector2f magnitude = new Vector2f();
	
	public void completeWall(Vector2f vEnd)	
	{
	 setEndOffset(vEnd, 0);	
	}
		
	public void setEnd(Vector2f vEnd)
	{
		setEndOffset(vEnd, offset);
	}
		
	private void setEndOffset(Vector2f vEnd, float wallOffset)
	{
		//System.out.println("wall box bbox " + box.getTransformedBoundingBox());
		end.set(vEnd);
		end.x -= dir.x * wallOffset;
		end.y -= dir.y * wallOffset;
		magnitude.sub(end, start);
		
		if (magnitude.dot(dir) <= 0)
		{
			//System.out.println("no wall yet, end:" + end);			
			return;
		}
				
		
		if (this.getChildCount() == 0)
		{
			//System.out.println("Add wall, end: " + end);
			this.addObject(box);
		}
		
		magnitude.scale(0.5f);		
		box.setTranslate(start.x + magnitude.x, Map.groundLevel + 1, start.y + magnitude.y);
		
		if(Math.abs(magnitude.x) <= 0.001f)
			magnitude.x = width;
		else if (Math.abs(magnitude.y) <= 0.001f)
			magnitude.y = width;
		else
		{
			System.out.println("Nonzero wall scale term error:" + magnitude);
			
		}
		box.setScale(magnitude.x, height, magnitude.y);
		
		//System.out.println("AFTER wall box bbox " + box.getTransformedBoundingBox());
		
		
		
	}

}
