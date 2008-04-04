package cs569.particle;

public class UpdaterParticleScale extends Updater {
	float startScale, endScale;
	float lifeBegin, lifeLength;
	
	public UpdaterParticleScale(float startScale, float endScale, float lifeBegin, float lifeLength)
	{
		this.lifeBegin = lifeBegin;
		this.lifeLength = lifeLength;
		this.startScale = startScale;
		this.endScale = endScale;
	}
	
	
	@Override
	public Particle update(Particle p, float currentTime)
	{
		
		float age = currentTime - p.timeBorn;
		if(lifeBegin < age && (age - lifeBegin)<lifeLength)
		{
			float x = Math.min((age - lifeBegin) / lifeLength, 1.0f);
			float s = (1 - x)*startScale + x*endScale;
	
			p.set(p.pos, p.velo, p.mass, p.color, p.timeBorn, s);
		}
		return p;
	}

}
