package cs569.object;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Quat4f;

import cs569.material.CookTorrance;
import cs569.material.AnisotropicWard;
import cs569.material.Lambertian;
import cs569.material.Phong;
import java.util.*;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class CustomScene implements ParameterizedObjectMaker {

	/**
	 * All inputs are ignored.
	 * 
	 * @see cs569.object.ParameterizedObjectMaker#make(java.lang.Object[])
	 */
	public final HierarchicalObject make(Object... inputs) {

		Scene out = new Scene();
		//RocketObject rocket = new RocketObject();
		//out.addObject(rocket.getGroup());
		
		MeshObject sphere = PrimitiveFactory.makeSphere(30, 30, "Sphere");
		//sphere.setTranslate(-0.6f, 0.3f, -0.6f);
		sphere.setScale(0.3f, 0.3f, 0.3f);
		sphere.setMaterial(new AnisotropicWard(new Color3f(0.2f, 0.3f, 0.1f),
				new Color3f(0.6f, 0.8f, 0.6f), 0.4f, 0.2f));
		out.addObject(sphere);
		
		return out;

	}
}
