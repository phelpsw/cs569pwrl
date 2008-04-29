package cs569.tron;

import java.io.PrintStream;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.material.Lambertian;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;

public class Map extends HierarchicalObject {
	
	public Map()
	{
		MeshObject plane = PrimitiveFactory.makePlane("Plane");
		//plane.setTranslate(0, -0.5f, 0);
		plane.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));		
		plane.setScale(10, 10, 10);
		this.addObject(plane);
	}
	
	
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException {	
		//draw walls		
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		// TODO Auto-generated method stub

	}
	
	public void addWall(Wall wall)
	{
		this.addObject(wall);
	}
	
	public void addVehicle(Vehicle vehicle)
	{
		this.addObject(vehicle);
	}

}
