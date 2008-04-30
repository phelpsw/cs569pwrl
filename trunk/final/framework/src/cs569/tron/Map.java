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
		MeshObject plane = PrimitiveFactory.makePlane("Ground", 100, 100);
		plane.setTranslate(0, -1f, 0);
		plane.setMaterial(new TexturedPhong("/textures/tron/floor.png"));
		plane.setScale(400, 400, 400);
		this.addObject(plane);
		
		MeshObject wall = PrimitiveFactory.makePlane("Wall1", 30, 1);
		wall.setRotationAxisAngle(-90, 1, 0, 0);
		wall.setTranslate(0, 10, 400);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(400, 1, 10);
		this.addObject(wall);		
		
		wall = PrimitiveFactory.makePlane("Wall2", 30, 1);
		wall.setRotationAxisAngle(90, 1, 0, 0);
		wall.mulRotation(180, 0, 1, 0); // flip right side up
		wall.setTranslate(0, 10, -400);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(400, 1, 10);
		this.addObject(wall);
		
		
		wall = PrimitiveFactory.makePlane("Wall3", 30, 1);
		wall.setRotationAxisAngle(90, 1, 0, 0);
		wall.mulRotation(90, 0, 0, 1);
		wall.mulRotation(180, 0, 1, 0); // flip right side up
		wall.setTranslate(400, 10, 0);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(400, 1, 10);
		this.addObject(wall);
		
		wall = PrimitiveFactory.makePlane("Wall4", 30, 1);
		wall.setRotationAxisAngle(-90, 0, 0, 1);
		wall.mulRotation(-90, 0, 1, 0); // flip right side up
		wall.setTranslate(-400, 10, 0);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(400, 1, 10);
		this.addObject(wall);
				
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
