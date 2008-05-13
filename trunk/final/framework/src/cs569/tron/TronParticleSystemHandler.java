package cs569.tron;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;
import cs569.particles.BillboardParticleSystem;
import cs569.particles.ExplosionBillboardEmitter;
import cs569.particles.ExplosionBillboardUpdater;
import cs569.particles.ExplosionParticleEmitter;
import cs569.particles.FireEmitter;
import cs569.particles.FireUpdater;
import cs569.particles.Gravity;
import cs569.particles.ParticleColorAttenuator;
import cs569.particles.ParticleSystem;
import cs569.texture.Texture;

public class TronParticleSystemHandler {
	ParticleSystem playerExplosionTriangleParticles = null;
	ParticleSystem playerExplosionBillboardBeams = null;
	ParticleSystem playerExplosionBillboardRings = null;
	
	
	public TronParticleSystemHandler()
	{
		
	}
	
	public void explodePlayer(Player player)
	{
		player.velocity = 0.0f;
		player.setVisible(false);
		
		playerExplosionTriangleParticles = new ParticleSystem(100);
		playerExplosionTriangleParticles.addForce(new Gravity(new Vector3f(0, 8, 0)));
		playerExplosionTriangleParticles.addEmitter(new ExplosionParticleEmitter(100, 10f, player.getVehicle().getTranslate()));
		playerExplosionTriangleParticles.addUpdater(new ParticleColorAttenuator(8.0f, new Color4f(1,1,0.5f,1), new Color4f(1, 0, 0, 0)));
		
		playerExplosionBillboardRings = new BillboardParticleSystem(Texture.getTexture("src/textures/tron/explode_rings.png"), true, 1);
		playerExplosionBillboardRings.addEmitter(new ExplosionBillboardEmitter(1f, player.getVehicle().getTranslate(), 1));
		playerExplosionBillboardRings.addUpdater(new ParticleColorAttenuator(1.0f, new Color4f(1,0.3f,0.3f,1), new Color4f(1, 0, 0, 0)));
		playerExplosionBillboardRings.addUpdater(new ExplosionBillboardUpdater());
		
		playerExplosionBillboardBeams = new BillboardParticleSystem(Texture.getTexture("src/textures/tron/explode_beams.png"), true, 1);
		playerExplosionBillboardBeams.addEmitter(new ExplosionBillboardEmitter(1f, player.getVehicle().getTranslate(), 1));
		playerExplosionBillboardBeams.addUpdater(new ParticleColorAttenuator(1.0f, new Color4f(1,1,0.2f,1), new Color4f(1, 1, 1, 0)));
		playerExplosionBillboardBeams.addUpdater(new ExplosionBillboardUpdater());

	}
	
	public void update(float time)
	{
		if(playerExplosionTriangleParticles != null)
			playerExplosionTriangleParticles.update(time);
		
		if(playerExplosionBillboardBeams != null)
			playerExplosionBillboardBeams.update(time);
		
		if(playerExplosionBillboardRings != null)
			playerExplosionBillboardRings.update(time);
	}
	
	public void glRender(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		if(playerExplosionTriangleParticles != null)
			playerExplosionTriangleParticles.glRender(gl, glu, eye);
		
		if(playerExplosionBillboardBeams != null)
			playerExplosionBillboardBeams.glRender(gl, glu, eye);
		
		if(playerExplosionBillboardRings != null)
			playerExplosionBillboardRings.glRender(gl, glu, eye);
	}
}
