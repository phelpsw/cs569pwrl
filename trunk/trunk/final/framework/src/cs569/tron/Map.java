package cs569.tron;

import java.io.PrintStream;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.material.AnisotropicWard;
import cs569.material.Lambertian;
import cs569.material.TexturedPhong;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;

public class Map extends HierarchicalObject {
	
	public Map()
	{
		
		/*
		MeshObject plane = PrimitiveFactory.makePlane("Plane");
		plane.setTranslate(0, -0.5f, 0);
		plane.setMaterial(new AnisotropicWard(new Color3f(0.2f, 0.3f, 0.1f),
				new Color3f(0.6f, 0.8f, 0.6f), 0.4f, 0.2f));
		//plane.setMaterial(new TexturedPhong());
		//plane.setScale(400, 400, 400);
		plane.setScale(100, 100, 100);
		this.addObject(plane);
		*/
		
		
		MeshObject plane = PrimitiveFactory.makePlane("Ground", 30, 30);
		plane.setTranslate(0, -1f, 0);
		plane.setMaterial(new TexturedPhong());
		plane.setScale(100, 100, 100);
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
	

}
