package cs569.particle;

import java.util.ArrayList;

public class ParticleSystem {
	protected ArrayList<Particle> particles;
	protected ArrayList<Force> forces;
	
	protected Integrator integrator;
	
	private float last_refresh_time;
	
	public ParticleSystem()
	{
		last_refresh_time = 0.0f;
		particles = new ArrayList<Particle>();
		forces = new ArrayList<Force>();
		
		//integrator = new IntegratorEuler();
		integrator = new IntegratorMidpoint();
		//integrator = new IntegratorRK4();
	}
	
	public void refresh(float time)
	{
		float dt;
		if(last_refresh_time == 0.0f)
			dt = 0.0f;
		else
			dt = time - last_refresh_time;
		last_refresh_time = time;
		
		for(Particle p:particles)
		{
			p.set(integrator.update(p, forces, time, dt));
		}
	}
}
