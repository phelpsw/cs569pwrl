package cs569.particle;

import java.util.Random;

import javax.vecmath.Vector3f;

public class UpdaterAgeRestart extends Updater {
	float max_age;  // in seconds
	Random generator;
	Vector3f pos;
	Vector3f velo;
	float velo_variance;
	float age_variance;
	
	public UpdaterAgeRestart(Vector3f pos, Vector3f velo, float velo_variance, float age_variance, float max_age)
	{
		this.pos = pos;
		this.velo = velo;
		this.velo_variance = velo_variance;
		this.max_age = max_age;
		this.age_variance = age_variance;
		generator = new Random();
	}
	
	public Particle update(Particle p, float currentTime)
	{
		if(currentTime - p.timeBorn > max_age)
		{
			float rx = (float)generator.nextGaussian();//*2.0f - 1.0f;
			float ry = (float)generator.nextGaussian();//*2.0f - 1.0f;
			float rz = (float)generator.nextGaussian();//*2.0f - 1.0f;
			
			Vector3f nVelo = new Vector3f();
			nVelo.x = rx*(velo_variance*velo.x);
			nVelo.y = ry*(velo_variance*velo.y);
			nVelo.z = rz*(velo_variance*velo.z);
			
			//float age = (int)((Math.max(Math.min(generator.nextGaussian(), 1.0f),-1.0f) / 2.0f + 1.0f)*(age_variance*max_age));
			
			p.set(pos, nVelo, p.mass, p.color, currentTime);
			return p;
		} else {			
			return p;
		}
	}

	
}
