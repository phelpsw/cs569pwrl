package cs569.particle;

import javax.vecmath.Vector3f;

public class UpdaterColorMorph extends Updater {
	float startR, endR;
	float startG, endG;
	float startB, endB;
	int life;
	
	public UpdaterColorMorph(float sR, float eR, float sG, float eG, float sB, float eB, int life )
	{
		this.startR = sR;
		this.endR = eR;
		this.startG = sG;
		this.endG = eG;
		this.startB = sB;
		this.endB = eB;
		this.life = life;
	}
	
	public Particle update(Particle p)
	{
		float x = Math.min((float)p.age / (float)life, 1.0f);
		
		float r = startR + x*(endR - startR);
		float g = startG + x*(endG - startG);
		float b = startB + x*(endB - startB);
		
		return new Particle(p.pos, p.velo, p.mass, new Vector3f(r,g,b), p.age);
	}
}
