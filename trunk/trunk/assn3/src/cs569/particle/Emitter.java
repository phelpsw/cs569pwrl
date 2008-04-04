package cs569.particle;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import cs569.texture.Texture;

public abstract class Emitter {
	public ParticleSystem ps;
	protected ArrayList<Updater> updaters;
	public Vector3f cameraPos;
	
	public abstract void refresh(float time);
	public abstract ArrayList<Particle> getParticles();
	public abstract void addUpdater(Updater u);
	public abstract void addForce(Force f);
	public abstract Texture getTexture();	
}
