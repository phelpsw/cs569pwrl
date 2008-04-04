package cs569.particle;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

public class IntegratorMidpoint extends Integrator {

	// http://groups.csail.mit.edu/graphics/classes/6.837/F04/assignments/assignment9/
	
	@Override
	public Particle update(Particle p, ArrayList<Force> forces, float time, float dt) {
		
		Vector3f Vm = new Vector3f(calcA(forces, p.pos, p.velo, p.mass, time));
		Vm.scale(dt/2.0f);
		Vm.add(p.velo);
		
		Vector3f Pm = new Vector3f(p.velo);
		Pm.scale(dt/2.0f);
		Pm.add(p.pos);
		
		Vector3f Vn1 = new Vector3f(calcA(forces, Pm, Vm, p.mass, time + dt/2.0f));
		Vn1.scale(dt);
		Vn1.add(p.velo);
		
		Vector3f Pn1 = new Vector3f(Vm);
		Pn1.scale(dt);
		Pn1.add(p.pos);
		
		p.set(Pn1,Vn1,p.mass,p.color,p.timeBorn);
		return p;
	}
	
	private Vector3f calcA(ArrayList<Force> forces, Vector3f pos, Vector3f velo, float mass, float time)
	{
		Vector3f ftot = new Vector3f();
		
		for(Force f: forces)
		{
			ftot.add(f.update(pos, velo, mass, time));
		}
		ftot.scale(1.0f/mass); // delta v
		
		return new Vector3f(ftot); // acceleration
	}

}
