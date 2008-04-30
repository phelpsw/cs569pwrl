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
import cs569.object.Group;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.PrimitiveFactory;

public class Vehicle extends HierarchicalObject {

	public Vehicle()
	{
		Group bike = new Group("LightCycle");
		
		//MeshObject box = PrimitiveFactory.makeBox("Box");
		//box.setMaterial(new Lambertian(new Color3f(1f, 0.05f, 0.05f)));
		this.addObject(bike);
		
		this.setRotationAxisAngle(-90.0f, 0.0f, 1.0f, 0.0f);
		this.setScale(4.0f, 4.0f, 4.0f);
		
		MeshObject cylinder_body = PrimitiveFactory.makeCylinder(10, 30, "Body");
		cylinder_body.setTranslate(0.0f, 0.0f, 0.0f);
		cylinder_body.setScale(0.75f, 0.1f, 0.27f);
		cylinder_body.setRotationAxisAngle(90.0f, 1.0f, 0.0f, 0.0f);
		bike.addObject(cylinder_body);
		
		MeshObject cylinder_fwheel = PrimitiveFactory.makeCylinder(10, 30, "FWheel");
		cylinder_fwheel.setTranslate(0.75f, -0.1f, 0.0f);
		cylinder_fwheel.setScale(0.2f, 0.2f, 0.2f);
		cylinder_fwheel.setRotationAxisAngle(90.0f, 1.0f, 0.0f, 0.0f);
		bike.addObject(cylinder_fwheel);
		
		MeshObject sphere_fhub = PrimitiveFactory.makeSphere(10, 30, "FHub");
		sphere_fhub.setTranslate(0.75f, -0.1f, 0.0f);
		sphere_fhub.setScale(0.1f, 0.1f, 0.27f);
		bike.addObject(sphere_fhub);
		
		MeshObject cylinder_bwheel = PrimitiveFactory.makeCylinder(10, 30, "BWheel");
		cylinder_bwheel.setTranslate(-0.75f, -0.1f, 0.0f);
		cylinder_bwheel.setScale(0.2f, 0.2f, 0.2f);
		cylinder_bwheel.setRotationAxisAngle(90.0f, 1.0f, 0.0f, 0.0f);
		bike.addObject(cylinder_bwheel);
		
		MeshObject sphere_rhub = PrimitiveFactory.makeSphere(10, 30, "RHub");
		sphere_rhub.setTranslate(-0.75f, -0.1f, 0.0f);
		sphere_rhub.setScale(0.1f, 0.1f, 0.27f);
		bike.addObject(sphere_rhub);
		
		MeshObject box_engine = PrimitiveFactory.makeBox("Engine");
		box_engine.setTranslate(-0.2f, -.08f, 0.0f);
		box_engine.setScale(0.6f, 0.20f, 0.09f);
		bike.addObject(box_engine);
		
		MeshObject box_fengine = PrimitiveFactory.makeBox("FEngine");
		box_fengine.setTranslate(0.4f, -.18f, 0.0f);
		box_fengine.setScale(0.4f, 0.1f, 0.09f);
		bike.addObject(box_fengine);
		
		MeshObject cone_lwindow = PrimitiveFactory.makeCone(10, 30, "LWindow");
		cone_lwindow.setTranslate(0.5f, 0.07f, -0.07f);
		cone_lwindow.setScale(0.1f, 0.25f, 0.1f);
		cone_lwindow.setRotationAxisAngle(-105.0f, 0.0f, 0.0f, 1.0f);
		cone_lwindow.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		bike.addObject(cone_lwindow);
		
		MeshObject cone_rwindow = PrimitiveFactory.makeCone(10, 30, "RWindow");
		cone_rwindow.setTranslate(0.5f, 0.07f, 0.07f);
		cone_rwindow.setScale(0.1f, 0.25f, 0.1f);
		cone_rwindow.setRotationAxisAngle(-105.0f, 0.0f, 0.0f, 1.0f);
		cone_rwindow.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		bike.addObject(cone_rwindow);
		
		MeshObject sphere_twindow = PrimitiveFactory.makeSphere(30, 30, "TWindow");
		sphere_twindow.setTranslate(0.45f, 0.15f, 0.0f);
		sphere_twindow.setScale(0.25f, 0.12f, 0.1f);
		sphere_twindow.setRotationAxisAngle(-10.0f, 0.0f, 0.0f, 1.0f);
		sphere_twindow.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		bike.addObject(sphere_twindow);
		
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
	
	
}
