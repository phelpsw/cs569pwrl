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
	static float height = 1;
	static float width = .05f;
	Material material;
	MeshObject box;
	
	public Wall(Vector2f startPos)
	{
		start.set(startPos);
		end.set(startPos);
		
		name = "WallTrail " + start.x + " " + start.y;
	
		box = PrimitiveFactory.makeBox("Box");
		box.setTranslate(startPos.x, 0.0f, startPos.y);
		//box.setScale(0.2f, 0.2f, 0.2f);
		box.setMaterial(new Lambertian(new Color3f(0.05f, 1.0f, 0.05f)));		
		this.addObject(box);
	}
		
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException {
		//draw a box.  No super
		//TODO draw wall.  Use box primitive
		
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		// TODO Auto-generated method stub
	}
	
	Vector2f magnitude = new Vector2f();
	
	public void setEnd(Vector2f vEnd)
	{
		end.set(vEnd);
		magnitude.sub(end, start);
		
		if (end.equals(start))
			return;
		
		magnitude.scale(0.5f);		
		box.setTranslate(start.x + magnitude.x, 0, start.y + magnitude.y);
		
		if(Math.abs(magnitude.x) <= 0.001f)
			magnitude.x = width;
		else if (Math.abs(magnitude.y) <= 0.001f)
			magnitude.y = width;
		else
		{
			System.out.println("Nonzero wall scale term error:" + magnitude);
			System.exit(1);
		}
		box.setScale(magnitude.x, height, magnitude.y);
		
		
		
	}

}
