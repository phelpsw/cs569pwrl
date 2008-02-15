package cs569.object;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import cs569.material.CookTorrance;
import cs569.material.AnisotropicWard;
import cs569.material.Lambertian;
import cs569.material.Phong;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class DefaultScene implements ParameterizedObjectMaker {

	/**
	 * All inputs are ignored.
	 * 
	 * @see cs569.object.ParameterizedObjectMaker#make(java.lang.Object[])
	 */
	public final HierarchicalObject make(Object... inputs) {

		Scene out = new Scene();

		// Make box and plane
		MeshObject plane = PrimitiveFactory.makePlane("Plane");
		plane.setTranslate(0, -0.5f, 0);
		plane.setMaterial(new Lambertian(new Color3f(0.2f, 0.2f, 0.2f)));
		// plane.setMaterial(new MipMapTexturedLambertian("chessboard-R.png"));
		MeshObject box = PrimitiveFactory.makeBox("Box");
		box.setTranslate(-0.6f, -0.25f, -0.6f);
		box.setScale(0.2f, 0.2f, 0.2f);
		box.setMaterial(new Lambertian(new Color3f(0.05f, 0.05f, 0.05f)));
		// box.setMaterial(new TexturedLambertian("chessboard-R.png"));
		MeshObject sphere = PrimitiveFactory.makeSphere(30, 30, "Sphere");
		sphere.setTranslate(-0.6f, 0.3f, -0.6f);
		sphere.setScale(0.3f, 0.3f, 0.3f);
		sphere.setMaterial(new AnisotropicWard(new Color3f(0.2f, 0.3f, 0.1f),
				new Color3f(0.6f, 0.8f, 0.6f), 0.4f, 0.2f));
		out.addObject(box);
		out.addObject(plane);
		out.addObject(sphere);

		// Make columns
		Group columns = new Group("Columns");
		for (int i = 0; i < 5; i++) {
			MeshObject cylinder = PrimitiveFactory.makeCylinder(10, 30,
					"Cylinder " + (i + 1));
			cylinder.setTranslate(-1.0f + 2.0f * ((i + 0.5f) / 5.0f), 0.0f,
					0.0f);
			cylinder.setScale(0.1f, 0.5f, 0.1f);
			columns.addObject(cylinder);
		}
		columns.setTranslate(0.0f, 0.0f, 0.8f);
		columns.setMaterial(new CookTorrance(new Color3f(0.8f, 0.55f, 0.3f),
				new Color3f(0.8f, 0.55f, 0.3f), 0.6f, 1.7f));
		out.addObject(columns);

		// Make Ovoid Tetrahedron Base
		float twoCos30 = (float) (2.0 * Math.cos(Math.toRadians(30)) * 0.3);
		float centerVertDist = (float) (0.15 / Math.sin(Math.toRadians(60)));
		float declineAngle = (float) (90.0 - Math.toDegrees(Math
				.asin(centerVertDist) / 0.3));

		Matrix4f rotation = new Matrix4f();
		Vector3f p = new Vector3f();

		Group tetraBase = new Group("TetraBase");
		MeshObject tetra1 = PrimitiveFactory.makeSphere(30, 30, "Tetra 1");

		tetra1.setRotationAxisAngle(90.0f, 0.0f, 1.0f, 0.0f);
		rotation.set(tetra1.getRotation());
		p.set(0.0f, 0.0f, twoCos30); rotation.transform(p); tetra1.setTranslate(p);
		tetra1.setScale(0.3f, 0.05f, 0.05f);

		MeshObject tetra2 = PrimitiveFactory.makeSphere(30, 30, "Tetra 2");
		tetra2.setRotationAxisAngle(-30.0f, 0.0f, 1.0f, 0.0f);
		rotation.set(tetra2.getRotation());
		p.set(.3f, 0.0f, 0.0f); rotation.transform(p); tetra2.setTranslate(p);
		tetra2.setScale(0.3f, 0.05f, 0.05f);
		MeshObject tetra3 = PrimitiveFactory.makeSphere(30, 30, "Tetra 3");
		tetra3.setRotationAxisAngle(30.0f, 0.0f, 1.0f, 0.0f);
		rotation.set(tetra3.getRotation());
		p.set(.3f, 0.0f, 0.0f); rotation.transform(p); tetra3.setTranslate(p);
		tetra3.setScale(0.3f, 0.05f, 0.05f);
		tetraBase.addObject(tetra1);
		tetraBase.addObject(tetra2);
		tetraBase.addObject(tetra3);

	
		// Make Ovoid Tetrahedron Top
		Group tetraTop = new Group("TetraTop");
		MeshObject tetra4 = PrimitiveFactory.makeSphere(30, 30, "Tetra 4");
		tetra4.setRotationAxisAngle(declineAngle, 0.0f, 0.0f, 1.0f);
		rotation.set(tetra4.getRotation());
		p.set(-0.3f, 0.0f, 0.0f); rotation.transform(p); tetra4.setTranslate(p);
		tetra4.setScale(0.3f, 0.05f, 0.05f);
		MeshObject tetra5 = PrimitiveFactory.makeSphere(30, 30, "Tetra 5");
		tetra5.setRotationAxisAngle(120.0f, 0.0f, 1.0f, 0.0f);
		tetra5.mulRotation(declineAngle, 0.0f, 0.0f, 1.0f);
		rotation.set(tetra5.getRotation());
		p.set(-0.3f, 0.0f, 0.0f); rotation.transform(p); tetra5.setTranslate(p);
		tetra5.setScale(0.3f, 0.05f, 0.05f);
		MeshObject tetra6 = PrimitiveFactory.makeSphere(30, 30, "Tetra 6");
		tetra6.setRotationAxisAngle(240.0f, 0.0f, 1.0f, 0.0f);
		tetra6.mulRotation(declineAngle, 0.0f, 0.0f, 1.0f);
		rotation.set(tetra6.getRotation());
		p.set(-0.3f, 0.0f, 0.0f); rotation.transform(p); tetra6.setTranslate(p);
		tetra6.setScale(0.3f, 0.05f, 0.05f);
		tetraTop.addObject(tetra4);
		tetraTop.addObject(tetra5);
		tetraTop.addObject(tetra6);
		tetraTop.setTranslate(2 * centerVertDist, twoCos30, 0.0f);

		// Make tetra
		Group tetra = new Group("Tetra");
		tetra.addObject(tetraBase);
		tetra.addObject(tetraTop);
		tetra.setTranslate(0.0f, -twoCos30 + 0.09f, -0.25f);
		tetra.setScale(1.25f, 1.25f, 1.25f);
		tetra.setMaterial(new Phong(new Color3f(0.2f, 0.2f, 0.5f),
				new Color3f(1.0f, 1.0f, 1.0f), 20.0f));
		
		out.addObject(tetra);

		return out;

	}
}
