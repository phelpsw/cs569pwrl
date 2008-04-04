package cs569.particle;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

public class IntegratorRK4 extends Integrator {

	@Override
	public Particle update(Particle p, ArrayList<Force> forces, float time, float dt) {
		
		Vector3f v_k1 = calcA(forces, p.pos, p.velo, p.mass, time);
		
		Vector3f V2 = new Vector3f(v_k1);
		V2.scale(dt/2.0f);
		V2.add(p.velo);
		
		Vector3f Pm = new Vector3f(p.velo);
		Pm.scale(dt/2.0f);
		Pm.add(p.pos);
		
		Vector3f v_k2 = calcA(forces, p.pos, V2, p.mass, time + dt/2.0f);
		
		Vector3f V3 = new Vector3f(v_k2);
		V3.scale(dt/2.0f);
		V3.add(p.velo);
		
		Vector3f v_k3 = calcA(forces, p.pos, V3, p.mass, time + dt/2.0f);
		
		Vector3f V4 = new Vector3f(v_k3);
		V3.scale(dt);
		V3.add(p.velo);
		
		Vector3f v_k4 = calcA(forces, p.pos, V4, p.mass, time + dt);
		
		V2.scale(2.0f);
		V3.scale(2.0f);
		
		Vector3f Pn1 = new Vector3f(p.velo);
		Pn1.add(V2);
		Pn1.add(V3);
		Pn1.add(V4);
		Pn1.scale(dt/6.0f);
		Pn1.add(p.pos);
		
		v_k2.scale(2.0f);
		v_k3.scale(2.0f);
		
		Vector3f Vn1 = new Vector3f(v_k1);
		Vn1.add(v_k2);
		Vn1.add(v_k3);
		Vn1.add(v_k4);
		Vn1.scale(dt/6.0f);
		Vn1.add(p.velo);
		
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
