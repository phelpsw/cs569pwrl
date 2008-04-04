package cs569.particle;

import javax.vecmath.Vector3f;

public class UpdaterColorMorph extends Updater {
	float startR, endR;
	float startG, endG;
	float startB, endB;
	float life;
	
	public UpdaterColorMorph(float sR, float eR, float sG, float eG, float sB, float eB, float life )
	{
		this.startR = sR;
		this.endR = eR;
		this.startG = sG;
		this.endG = eG;
		this.startB = sB;
		this.endB = eB;
		this.life = life;
	}
	
	public Particle update(Particle p, float currentTime)
	{
		float age = currentTime - p.timeBorn;
		float x = Math.min(age / life, 1.0f);
		
		float r = startR + x*(endR - startR);
		float g = startG + x*(endG - startG);
		float b = startB + x*(endB - startB);
		p.set(p.pos, p.velo, p.mass, new Vector3f(r,g,b), p.timeBorn, p.scale);
		return p;
	}

}
