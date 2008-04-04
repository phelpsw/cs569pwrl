package cs569.particle;

import java.util.ArrayList;
import javax.vecmath.Vector3f;

import cs569.texture.Texture;

import java.util.Random;

public class EmitterPoint extends Emitter {
	private Texture texture;
	
	public EmitterPoint(int count, Vector3f pos, Vector3f velo, float velo_variance, Texture t)
	{
		Random generator = new Random();
	
		texture = t;
		
		ps = new ParticleSystem();
		updaters = new ArrayList<Updater>();
		
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
	}
	
	@Override
	public void refresh(float time) {
		ps.refresh(time);
		
		for(Particle p: ps.particles)
		{
			for(Updater u:updaters)
			{
				p.set(u.update(p, time));
			}
		}
		
	}
	
	@Override
	public ArrayList<Particle> getParticles() {
		return ps.particles;
	}

	@Override
	public void addForce(Force f) {
		ps.forces.add(f);
	}

	@Override
	public void addUpdater(Updater u) {
		updaters.add(u);
		
	}

	@Override
	public Texture getTexture() {
		return texture;
	}

}
