package cs569.tron;

import java.io.IOException;
import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.misc.GLSLErrorException;
import cs569.misc.OBJLoaderException;
import cs569.object.Group;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;

public class Vehicle extends HierarchicalObject {
	
	private Group bike_body;
	private Group bike_window;
	private Group bike_hub;
	private Group bike_wheel;
	
	private static final float LIGHT_CYCLE_SCALE = .01f;
	
	public Vehicle()
	{
		Group bike = new Group("LightCycle");
		
		this.addObject(bike);
		
		//this.setRotationAxisAngle(90.0f, 0.0f, 1.0f, 0.0f);
		this.setScale(LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE);		
		
		bike_body = new Group("LightCycle_Body");
		bike_window = new Group("LightCycle_Window");
		bike_hub = new Group("LightCycle_Hub");
		bike_wheel = new Group("LightCycle_Wheel");
		
				
		bike.addObject(bike_body);
		bike.addObject(bike_window);
		bike.addObject(bike_hub);
		bike.addObject(bike_wheel);
		
		
		try {			
			bike_body.addObject(MeshObject.loadFromOBJ("models/bodyColor.obj", false));	
			bike_window.addObject(MeshObject.loadFromOBJ("models/windows.obj", false));
			bike_hub.addObject(MeshObject.loadFromOBJ("models/bodyFrame.obj", false));			
			bike_wheel.addObject(MeshObject.loadFromOBJ("models/wheelwells.obj", false));								
		} catch (Exception e)
		{
			System.out.println("Unable to load bike object");
		}
		
		
		
		/*
		float shift = 0;
		
		MeshObject cylinder_body = PrimitiveFactory.makeCylinder(10, 30, "Body");
		cylinder_body.setTranslate(0.0f + shift, 0.0f, 0.0f);		
		cylinder_body.setScale(0.75f, 0.1f, 0.27f);
		cylinder_body.setRotationAxisAngle(90.0f, 1.0f, 0.0f, 0.0f);		
		bike_body.addObject(cylinder_body);
		
		MeshObject cylinder_fwheel = PrimitiveFactory.makeCylinder(10, 30, "FWheel");
		cylinder_fwheel.setTranslate(0.75f + shift, -0.1f, 0.0f);
		cylinder_fwheel.setScale(0.2f, 0.2f, 0.2f);
		cylinder_fwheel.setRotationAxisAngle(90.0f, 1.0f, 0.0f, 0.0f);
		bike_wheel.addObject(cylinder_fwheel);
		
		MeshObject sphere_fhub = PrimitiveFactory.makeSphere(10, 30, "FHub");
		sphere_fhub.setTranslate(0.75f + shift, -0.1f, 0.0f);
		sphere_fhub.setScale(0.1f, 0.1f, 0.27f);
		bike_hub.addObject(sphere_fhub);
		
		MeshObject cylinder_bwheel = PrimitiveFactory.makeCylinder(10, 30, "BWheel");
		cylinder_bwheel.setTranslate(-0.75f + shift, -0.1f, 0.0f);
		cylinder_bwheel.setScale(0.2f, 0.2f, 0.2f);
		cylinder_bwheel.setRotationAxisAngle(90.0f, 1.0f, 0.0f, 0.0f);
		bike_wheel.addObject(cylinder_bwheel);
		
		MeshObject sphere_rhub = PrimitiveFactory.makeSphere(10, 30, "RHub");
		sphere_rhub.setTranslate(-0.75f + shift, -0.1f, 0.0f);
		sphere_rhub.setScale(0.1f, 0.1f, 0.27f);
		bike_hub.addObject(sphere_rhub);
		
		MeshObject box_engine = PrimitiveFactory.makeBox("Engine");
		box_engine.setTranslate(-0.2f + shift, -.08f, 0.0f);
		box_engine.setScale(0.6f, 0.20f, 0.09f);
		bike_body.addObject(box_engine);
		
		MeshObject box_fengine = PrimitiveFactory.makeBox("FEngine");
		box_fengine.setTranslate(0.4f + shift, -.18f, 0.0f);
		box_fengine.setScale(0.4f, 0.1f, 0.09f);
		bike_body.addObject(box_fengine);
		
		MeshObject cone_lwindow = PrimitiveFactory.makeCone(10, 30, "LWindow");
		cone_lwindow.setTranslate(0.5f + shift, 0.07f, -0.07f);
		cone_lwindow.setScale(0.1f, 0.25f, 0.1f);
		cone_lwindow.setRotationAxisAngle(-105.0f, 0.0f, 0.0f, 1.0f);
		cone_lwindow.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		bike_window.addObject(cone_lwindow);
		
		MeshObject cone_rwindow = PrimitiveFactory.makeCone(10, 30, "RWindow");
		cone_rwindow.setTranslate(0.5f + shift, 0.07f, 0.07f);
		cone_rwindow.setScale(0.1f, 0.25f, 0.1f);
		cone_rwindow.setRotationAxisAngle(-105.0f, 0.0f, 0.0f, 1.0f);
		cone_rwindow.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		bike_window.addObject(cone_rwindow);
		
		MeshObject sphere_twindow = PrimitiveFactory.makeSphere(30, 30, "TWindow");
		sphere_twindow.setTranslate(0.45f + shift, 0.15f, 0.0f);
		sphere_twindow.setScale(0.25f, 0.12f, 0.1f);
		sphere_twindow.setRotationAxisAngle(-10.0f, 0.0f, 0.0f, 1.0f);
		sphere_twindow.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		bike_window.addObject(sphere_twindow);
		
		*/
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
		Quat4f rot = this.getRotation();
		rot.mul(q);
		this.setRotation(rot);
	}
	
	public void setHubMaterial(Material in)
	{
		bike_hub.setMaterial(in);
	}
	
	public void setWheelMaterial(Material in)
	{
		bike_wheel.setMaterial(in);
	}
	
	
	public void setWindowMaterial(Material in)
	{
		bike_window.setMaterial(in);
	}
	
	public void setBodyMaterial(Material in)
	{
		bike_body.setMaterial(in);
	}
	
	public Material getBodyMaterial()
	{
		return bike_body.getMaterial();
	}
	
	public void setVisible(boolean val)
	{
		/*
		if(val)
		{
			//this.setScale(LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE);
			bike_body.setScale(LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE);
			bike_window.setScale(LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE);
			bike_hub.setScale(LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE);
			bike_wheel.setScale(LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE, LIGHT_CYCLE_SCALE);
		} else {
			//this.setScale(0,0,0);
			bike_body.setScale(0,0,0);
			bike_window.setScale(0,0,0);
			bike_hub.setScale(0,0,0);
			bike_wheel.setScale(0,0,0);
		}
		*/
	}
}
