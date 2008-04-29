package cs569.tron;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.material.Material;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;

public class Wall extends HierarchicalObject {

	Vector2f start = new Vector2f();
	Vector2f end = new Vector2f();
	static float height = 10;
	static float width = .4f;
	Material material;
	
	public Wall(Vector2f startPos)
	{
		start.set(startPos);
		end.set(startPos);
		
		
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
	
	public void setEnd(Vector2f vEnd)
	{
		end.set(vEnd);
	}

}
