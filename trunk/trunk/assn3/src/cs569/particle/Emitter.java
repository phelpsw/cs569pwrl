package cs569.particle;

import java.util.ArrayList;

public abstract class Emitter {
	
	public ParticleSystem ps;
	protected ArrayList<Updater> updaters;
	
	public abstract void refresh(float time);
	public abstract ArrayList<Particle> getParticles();
}
