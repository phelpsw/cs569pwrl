package cs569.particle;

import java.util.ArrayList;
import javax.vecmath.Vector3f;
import java.util.Random;

public class EmitterPoint extends Emitter {

	public EmitterPoint(int count, Vector3f pos, Vector3f velo, float velo_variance)
	{
		Random generator = new Random();
		
		ps = new ParticleSystem();
		updaters = new ArrayList<Updater>();
		
		UpdaterAgeRestart agr = new UpdaterAgeRestart(pos, velo, velo_variance, 1.0f, 30);
		UpdaterColorMorph cm = new UpdaterColorMorph(1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 30);
		updaters.add(agr);
		updaters.add(cm);
		
		for(int i=0; i<count; i++)
		{
			float rx = (float)generator.nextGaussian()*2.0f - 1.0f;
			float ry = (float)generator.nextGaussian()*2.0f - 1.0f;
			float rz = (float)generator.nextGaussian()*2.0f - 1.0f;
			
			Vector3f nVelo = new Vector3f();
			nVelo.x = rx*(velo_variance*velo.x);
			nVelo.y = ry*(velo_variance*velo.y);
			nVelo.z = rz*(velo_variance*velo.z);
			
			Particle p = new Particle(pos, nVelo, 0.1f, new Vector3f(1.0f,0.0f,0.0f),0);
			ps.particles.add(p);
		}
		ForceWind fwind = new ForceWind(new Vector3f(0.0f,0.0f,-1.0f),1.0f,3.0f,0.1f);
		ForceGravity fgrav = new ForceGravity();
		ps.forces.add(fgrav);
		ps.forces.add(fwind);
	}
	
	@Override
	public void refresh(float time) {
		ps.refresh(time);
		
		for(Particle p: ps.particles)
		{
			for(Updater u:updaters)
			{
				p.set(u.update(p));
			}
		}
		
	}
	
	@Override
	public ArrayList<Particle> getParticles() {
		return ps.particles;
	}

}
