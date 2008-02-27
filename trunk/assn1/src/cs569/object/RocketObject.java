package cs569.object;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs569.material.AnisotropicWard;
import cs569.material.CookTorrance;
import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.misc.BoundingSphere;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.misc.WritingUtils;

public class RocketObject implements ParameterizedObjectMaker {

	int nose;
	int engines;
	int finNum;
	Group rGroup = new Group("Rocket");
	
	public final HierarchicalObject make(Object... inputs) {

		Scene out = new Scene();
		RocketObject rocket = new RocketObject();
		out.addObject(rocket.getGroup());
		
		return out;

	}
	/**
	 * 
	 * @param noseType valid values are 0: Pointed, 1: Large Payload, 2: Pointed Medium Payload.  Invalid values will result in no nosecone.
	 * @param engineCount valid values are 0 - 3, arrangement is automatically handled. An invalid value will result in no engines.
	 * @param finCount renders 2x the value of this parameter.  A value of 2 results in 4 evenly spaced fins.
	 */
	public RocketObject(int noseType, int engineCount, int finCount)
	{
		nose = noseType;
		engines = engineCount;
		finNum = finCount;
		construct();
	}
	public RocketObject()
	{
		nose = 1;
		engines = 3;
		finNum = 3;
		construct();
	}
	
	public Group getGroup()
	{ return rGroup; }
	
	public void construct() {
		if(nose == 0)
		{
			//Sharp nosecone
			MeshObject cone = PrimitiveFactory.makeCone(200,"NoseCone");
			cone.setTranslate(0.0f, 1.0f, 0.0f);
			cone.setScale(0.1f, 0.2f, 0.1f);
			cone.setMaterial(new CookTorrance(new Color3f(0.9f, 0.9f, 0.9f),
					new Color3f(0.6f, 0.6f, 0.6f), 0.8f, 1.5f));
			rGroup.addObject(cone);
		} else if (nose == 1) {
			// Large Payload nosecone
			MeshObject nosesphere = PrimitiveFactory.makeSphere(30, 30, "NoseSphere");
			nosesphere.setTranslate(0.0f, 1.0f, 0.0f);
			nosesphere.setScale(0.12f, 0.2f, 0.12f);
			nosesphere.setMaterial(new AnisotropicWard(new Color3f(0.2f, 0.2f, 0.2f),
					new Color3f(0.6f, 0.6f, 0.6f), 0.4f, 0.2f));
			rGroup.addObject(nosesphere);
			
			MeshObject nosecyl = PrimitiveFactory.makeCylinder(10, 200,"NoseBody");
			nosecyl.setTranslate(0.0f, 0.8f, 0.0f);
			nosecyl.setScale(0.12f, 0.2f, 0.12f);
			nosecyl.setMaterial(new AnisotropicWard(new Color3f(0.2f, 0.2f, 0.2f),
					new Color3f(0.6f, 0.6f, 0.6f), 0.4f, 0.2f));
			rGroup.addObject(nosecyl);
		} else if (nose == 2) {
			// Sharp Payload nosecone
			MeshObject cone = PrimitiveFactory.makeCone(200,"NoseCone");
			cone.setTranslate(0.0f, 1.0f, 0.0f);
			cone.setScale(0.12f, 0.15f, 0.12f);
			cone.setMaterial(new CookTorrance(new Color3f(0.9f, 0.9f, 0.9f),
					new Color3f(0.6f, 0.6f, 0.6f), 0.8f, 1.5f));
			rGroup.addObject(cone);
			
			MeshObject nosecyl = PrimitiveFactory.makeCylinder(10, 200,"NoseBody");
			nosecyl.setTranslate(0.0f, 0.75f, 0.0f);
			nosecyl.setScale(0.12f, 0.1f, 0.12f);
			nosecyl.setMaterial(new CookTorrance(new Color3f(0.9f, 0.9f, 0.9f),
					new Color3f(0.6f, 0.6f, 0.6f), 0.8f, 1.5f));
			rGroup.addObject(nosecyl);
		}
		
		
		MeshObject bodycyl = PrimitiveFactory.makeCylinder(10, 200,"Body");
		bodycyl.setScale(0.1f, 0.8f, 0.1f);
		bodycyl.setMaterial(new CookTorrance(new Color3f(0.5f, 0.5f, 0.6f),
				new Color3f(0.6f, 0.6f, 0.6f), 0.15f, 1.5f));
		rGroup.addObject(bodycyl);
		
		if(engines == 1)
		{
			// 1 engine
			MeshObject eng1 = PrimitiveFactory.makeCone(200,"Engine");
			eng1.setTranslate(0.0f, -0.85f, 0.0f);
			eng1.setScale(0.05f, 0.1f, 0.05f);
			eng1.setMaterial(new CookTorrance(new Color3f(0.1f, 0.1f, 0.1f),
					new Color3f(0.4f, 0.4f, 0.4f), 0.9f, 1.1f));
			rGroup.addObject(eng1);
		} else if (engines == 2) {
			// 2 engines
			MeshObject eng1 = PrimitiveFactory.makeCone(200,"Engine");
			eng1.setTranslate(-0.05f, -0.85f, 0.0f);
			eng1.setScale(0.05f, 0.1f, 0.05f);
			eng1.setMaterial(new CookTorrance(new Color3f(0.1f, 0.1f, 0.1f),
					new Color3f(0.4f, 0.4f, 0.4f), 0.9f, 1.1f));
			rGroup.addObject(eng1);
			
			MeshObject eng2 = PrimitiveFactory.makeCone(200,"Engine");
			eng2.setTranslate(0.05f, -0.85f, 0.0f);
			eng2.setScale(0.05f, 0.1f, 0.05f);
			eng2.setMaterial(new CookTorrance(new Color3f(0.1f, 0.1f, 0.1f),
					new Color3f(0.4f, 0.4f, 0.4f), 0.9f, 1.1f));
			rGroup.addObject(eng2);
		} else if (engines == 3) {
			// 3 engines
			MeshObject eng1 = PrimitiveFactory.makeCone(200,"Engine");
			eng1.setTranslate(-0.0433f, -0.85f, -0.0433f);
			eng1.setScale(0.03f, 0.07f, 0.03f);
			eng1.setMaterial(new CookTorrance(new Color3f(0.1f, 0.1f, 0.1f),
					new Color3f(0.4f, 0.4f, 0.4f), 0.9f, 1.1f));
			rGroup.addObject(eng1);
			
			MeshObject eng2 = PrimitiveFactory.makeCone(200,"Engine");
			eng2.setTranslate(0.0433f, -0.85f, -0.0433f);
			eng2.setScale(0.03f, 0.07f, 0.03f);
			eng2.setMaterial(new CookTorrance(new Color3f(0.1f, 0.1f, 0.1f),
					new Color3f(0.4f, 0.4f, 0.4f), 0.9f, 1.1f));
			rGroup.addObject(eng2);
			
			MeshObject eng3 = PrimitiveFactory.makeCone(200,"Engine");
			eng3.setTranslate(0.0f, -0.85f, 0.05f);
			eng3.setScale(0.03f, 0.07f, 0.03f);
			eng3.setMaterial(new CookTorrance(new Color3f(0.1f, 0.1f, 0.1f),
					new Color3f(0.4f, 0.4f, 0.4f), 0.9f, 1.1f));
			rGroup.addObject(eng3);
		}
		
		// fins
		ArrayList<MeshObject> fins = new ArrayList<MeshObject>();
		for(int i=0; i<finNum; i++) {
			float angle = (float)((( i / (float)finNum) * Math.PI)/(2*Math.PI) * 360.0);
			fins.add(PrimitiveFactory.makeBox("Fin"+i));
			fins.get(i).setRotationAxisAngle(angle, 0.0f, 1.0f, 0.0f);
			fins.get(i).setScale(0.2f, 0.05f,0.0001f);
			fins.get(i).setTranslate(0.0f, -0.7f, -0.0f);
			fins.get(i).setMaterial(new Lambertian(new Color3f(0.8f, 0.8f, 0.8f)));
			rGroup.addObject(fins.get(i));
		}
	}
}
