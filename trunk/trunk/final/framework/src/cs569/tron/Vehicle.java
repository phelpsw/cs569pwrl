package cs569.tron;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.material.Lambertian;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;

public class Vehicle extends HierarchicalObject {

	public Vehicle()
	{
		MeshObject box = PrimitiveFactory.makeBox("Box");
		box.setMaterial(new Lambertian(new Color3f(1f, 0.05f, 0.05f)));
		this.addObject(box);
		
		//TODO make vehicle out of primitives
		
	}
	
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException {
		// leave empty.  All drawing is done through children
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		// TODO Auto-generated method stub

	}

	public void setPos(Vector2f pos)
	{
		this.setTranslate(pos.x, 0, pos.y);	
	}
	
	public void addRotate(Quat4f q)
	{
	 this.getRotation().mul(q);
	}
	
	
}
