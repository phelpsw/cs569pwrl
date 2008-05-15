package cs569.tron;

import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.particles.BillboardParticleSystem;
import cs569.particles.ExplosionBillboardEmitter;
import cs569.particles.ExplosionBillboardUpdater;
import cs569.particles.ExplosionParticleEmitter;
import cs569.particles.ExplosionTriangleUpdater;
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
	ExplosionTriangleUpdater triupdater = null;
	MeshObject triDebris = null;
	
	static final int particleCount = 100;
	
	boolean exploded;
	
	public TronParticleSystemHandler(Player player, HierarchicalObject renderGroup)
	{
		triDebris = generateRandomTriangles(player.getVehicle().getTranslate(), particleCount, 0.5f);
		triDebris.setMaterial(player.vehicle.getBodyMaterial());
		renderGroup.addObject(triDebris);
		
		playerExplosionTriangleParticles = new ParticleSystem(particleCount);
		triupdater = new ExplosionTriangleUpdater(triDebris, playerExplosionTriangleParticles, particleCount, player.getVehicle().getTranslate());
		playerExplosionTriangleParticles.addForce(new Gravity(new Vector3f(0, -50, 0)));
		playerExplosionTriangleParticles.addEmitter(new ExplosionParticleEmitter(particleCount, 150f, player.getVehicle().getTranslate()));
		playerExplosionTriangleParticles.addUpdater(new ParticleColorAttenuator(8.0f, new Color4f(1,1,0.5f,1), new Color4f(1, 0, 0, 0)));
		
		playerExplosionBillboardRings = new BillboardParticleSystem(Texture.getTexture("src/textures/tron/explode_rings.png"), true, 1);
		playerExplosionBillboardRings.addEmitter(new ExplosionBillboardEmitter(1f, player.getVehicle().getTranslate(), 1));
		playerExplosionBillboardRings.addUpdater(new ParticleColorAttenuator(2.0f, new Color4f(1,0.3f,0.3f,1), new Color4f(1, 0, 0, 0)));
		playerExplosionBillboardRings.addUpdater(new ExplosionBillboardUpdater());
		
		playerExplosionBillboardBeams = new BillboardParticleSystem(Texture.getTexture("src/textures/tron/explode_beams.png"), true, 1);
		playerExplosionBillboardBeams.addEmitter(new ExplosionBillboardEmitter(1f, player.getVehicle().getTranslate(), 1));
		playerExplosionBillboardBeams.addUpdater(new ParticleColorAttenuator(1.0f, new Color4f(1,1,0.0f,1), new Color4f(1, 1, 1, 0)));
		playerExplosionBillboardBeams.addUpdater(new ExplosionBillboardUpdater());
		
		exploded = false;
	}
	
	public void explodePlayer(Player player)
	{
		exploded = true;
	}
	
	public MeshObject generateRandomTriangles(Vector3f pos, int count, float size)
	{
		float[] verts = new float[9*count];
		int[] tris = new int[3*count];
		float[] normals = new float[9*count];
		float[] texcoords = new float[6*count];
		Vector3f norm = new Vector3f();
		Random rand = new Random();
		
		for(int i=0; i<count; i++)
		{
			norm.set(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
			norm.normalize();
			tris[3*i] = 3*i;
			tris[3*i+1] = 3*i+1;
			tris[3*i+2] = 3*i+2;
			
			verts[9*i] = size*rand.nextFloat();
			verts[9*i+1] = size*rand.nextFloat();
			verts[9*i+2] = size*rand.nextFloat();
			
			verts[9*i+3] = size*rand.nextFloat();
			verts[9*i+4] = size*rand.nextFloat();
			verts[9*i+5] = size*rand.nextFloat();
			
			verts[9*i+6] = size*rand.nextFloat();
			verts[9*i+7] = size*rand.nextFloat();
			verts[9*i+8] = size*rand.nextFloat();
			
			normals[9*i] = norm.x;
			normals[9*i+1] = norm.y;
			normals[9*i+2] = norm.z;
			
			normals[9*i+3] = norm.x;
			normals[9*i+4] = norm.y;
			normals[9*i+5] = norm.z;
			
			normals[9*i+6] = norm.x;
			normals[9*i+7] = norm.y;
			normals[9*i+8] = norm.z;
			
			texcoords[6*i] = 0;
			texcoords[6*i+1] = 0;
			
			texcoords[6*i+2] = 0;
			texcoords[6*i+3] = 1;
			
			texcoords[6*i+4] = 0;
			texcoords[6*i+5] = 1;
		}
		
		return new MeshObject(verts, tris, normals, texcoords, "explosion");
	}
	
	public void update(float time)
	{
		if(exploded == false)
			return;
		
		if(playerExplosionTriangleParticles != null)
			playerExplosionTriangleParticles.update(time);
		
		if(triupdater!= null)
			triupdater.update(time);
		
		if(playerExplosionBillboardBeams != null)
			playerExplosionBillboardBeams.update(time);
		
		if(playerExplosionBillboardRings != null)
			playerExplosionBillboardRings.update(time);
	}
	
	public void glRender(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		/*
		if(playerExplosionTriangleParticles != null)
			playerExplosionTriangleParticles.glRender(gl, glu, eye);
		*/
		if(playerExplosionBillboardBeams != null)
			playerExplosionBillboardBeams.glRender(gl, glu, eye);
		
		if(playerExplosionBillboardRings != null)
			playerExplosionBillboardRings.glRender(gl, glu, eye);
	}
}
