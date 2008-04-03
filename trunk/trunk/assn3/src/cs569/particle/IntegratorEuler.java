package cs569.particle;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class IntegratorEuler extends Integrator {
	
	public Particle update(Particle p, ArrayList<Force> forces, float time, float dt)
	{
		
		Vector3f ftot = new Vector3f();
		for(Force f: forces)
		{
			ftot.add(f.update(p.pos, p.velo, p.mass, (float)0.0));
		}
		ftot.scale(1.0f/p.mass); // delta v
		
		Vector3f a = new Vector3f(ftot);
		
		Vector3f Vn1 = new Vector3f(a);
		Vn1.scale(dt);
		Vn1.add(p.velo);
		
		Vector3f Pn1 = new Vector3f(p.velo);
		Pn1.scale(dt);
		Pn1.add(p.pos);
		
		return new Particle(Pn1,Vn1,p.mass,p.color,p.age);
	}
}
