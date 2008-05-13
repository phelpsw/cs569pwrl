package cs569.tron;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;
import cs569.particles.ExplosionEmitter;
import cs569.particles.Gravity;
import cs569.particles.ParticleColorAttenuator;
import cs569.particles.ParticleSystem;

public class TronParticleSystemHandler {
	ParticleSystem particles = null;

	public TronParticleSystemHandler()
	{
		
	}
	
	public void explodePlayer(Player player)
	{
		player.velocity = 0.0f;
		particles = new ParticleSystem(100);
		particles.addForce(new Gravity(new Vector3f(0, -1, 0)));
		particles.addEmitter(new ExplosionEmitter(100, 5f, player.getVehicle().getTranslate()));
		particles.addUpdater(new ParticleColorAttenuator(8.0f, new Color4f(1,1,0.5f,1), new Color4f(1, 0, 0, 0)));
	}
	
	public void update(float time)
	{
		if(particles != null)
			particles.update(time);
	}
	
	public void glRender(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		if(particles != null)
			particles.glRender(gl, glu, eye);
	}
}
