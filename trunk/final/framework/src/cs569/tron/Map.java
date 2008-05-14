package cs569.tron;

import java.io.PrintStream;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.material.AnisotropicWard;
import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.material.ShadowedGlow;
import cs569.material.TexturedPhong;
import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;
import cs569.shaders.ShadowedGlowShader;
import cs569.texture.Texture;

public class Map extends HierarchicalObject {
	private MeshObject ground;
	
	public Map()
	{	
		float wallHeight = 20;
		float mapWidth = 200;
		float groundLevel = -1;
		int wallTiles = 8;
		
		ground = PrimitiveFactory.makePlane("Ground", 100, 100);
		ground.collidable = false;
		ground.setTranslate(0, groundLevel, 0);
		ground.setMaterial(new TexturedPhong("/textures/tron/floor.png"));
		ground.setScale(mapWidth, 1, mapWidth);
		this.addObject(ground);		
				
		
		MeshObject wall = PrimitiveFactory.makePlane("Wall1", wallTiles, 1);
		wall.collidable = true;
		wall.setRotationAxisAngle(-90, 1, 0, 0);
		wall.setTranslate(0, wallHeight + groundLevel, mapWidth);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(mapWidth, 1, wallHeight);
		this.addObject(wall);		
		
		wall = PrimitiveFactory.makePlane("Wall2", wallTiles , 1);
		wall.collidable = true; 
		wall.setRotationAxisAngle(90, 1, 0, 0);
		wall.mulRotation(180, 0, 1, 0); // flip right side up
		wall.setTranslate(0, wallHeight + groundLevel, -mapWidth);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(mapWidth, 1, wallHeight);
		this.addObject(wall);
				
		wall = PrimitiveFactory.makePlane("Wall3", wallTiles , 1);
		wall.collidable = true;
		wall.setRotationAxisAngle(90, 1, 0, 0);
		wall.mulRotation(90, 0, 0, 1);
		wall.mulRotation(180, 0, 1, 0); // flip right side up
		wall.setTranslate(mapWidth, wallHeight + groundLevel, 0);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(mapWidth, 1, wallHeight);
		this.addObject(wall);
		
		wall = PrimitiveFactory.makePlane("Wall4", wallTiles , 1);
		wall.collidable = true;
		wall.setRotationAxisAngle(-90, 0, 0, 1);
		wall.mulRotation(-90, 0, 1, 0); // flip right side up
		wall.setTranslate(-mapWidth, wallHeight + groundLevel, 0);
		wall.setMaterial(new TexturedPhong("/textures/tron/rim_wall.png")); //
		wall.setScale(mapWidth, 1, wallHeight);
		this.addObject(wall);
		
		MeshObject sky = PrimitiveFactory.makeSphere(10,10,"sky");
		sky.collidable = false;
		sky.flipNormals();
		sky.setScale(600, 600, 600);
		sky.setMaterial(new Lambertian(new Color3f(.3f,.3f,1)));
		this.addObject(sky);
		
				
	}
	
	public void setGroundMaterial(Material in)
	{
		ground.setMaterial(in);
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
