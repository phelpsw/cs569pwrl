package cs569.particles;

import java.nio.FloatBuffer;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import cs569.object.MeshObject;

public class ExplosionTriangleUpdater
{

	/* Time management */
	protected float lastTimeUpdated = -1.0f;
	
	MeshObject mesh;
	ParticleSystem ps;
	int count;
	Vector3f pos = new Vector3f();;
	
	public ExplosionTriangleUpdater(MeshObject mesh, ParticleSystem ps, int count)
	{
		this.mesh = mesh;
		this.ps = ps;
		this.count = count;
	}
	
	public void update(float time) 
	{
		FloatBuffer verts = mesh.getVertices();
		
		if (lastTimeUpdated < 0) {
			lastTimeUpdated = time;
			return;
		}
		
		float dt = time - lastTimeUpdated;
		
		Vector3f velocity = new Vector3f();
		
		float scaleFactor = 0.01f*dt;
		for(int i=0; i<count; i++)
		{
			velocity.set(ps.getParticleVelocity(i));
			verts.put(9*i, verts.get(9*i)+ scaleFactor*velocity.x);
			verts.put(9*i+1, verts.get(9*i+1)+ scaleFactor*velocity.y);
			verts.put(9*i+2, verts.get(9*i+2)+ scaleFactor*velocity.z);
			
			verts.put(9*i+3, verts.get(9*i+3)+ scaleFactor*velocity.x);
			verts.put(9*i+4, verts.get(9*i+4)+ scaleFactor*velocity.y);
			verts.put(9*i+5, verts.get(9*i+5)+ scaleFactor*velocity.z);
			
			verts.put(9*i+6, verts.get(9*i+6)+ scaleFactor*velocity.x);
			verts.put(9*i+7, verts.get(9*i+7)+ scaleFactor*velocity.y);
			verts.put(9*i+8, verts.get(9*i+8)+ scaleFactor*velocity.z);
		}
		lastTimeUpdated = time;
	}
	
	public void setPosition(Vector3f pos)
	{
		FloatBuffer verts = mesh.getVertices();
		this.pos = pos;
		for(int i=0; i<count; i++)
		{
			verts.put(9*i, verts.get(9*i)+ pos.x);
			verts.put(9*i+1, verts.get(9*i+1)+ pos.y);
			verts.put(9*i+2, verts.get(9*i+2)+ pos.z);
			
			verts.put(9*i+3, verts.get(9*i+3)+ pos.x);
			verts.put(9*i+4, verts.get(9*i+4)+ pos.y);
			verts.put(9*i+5, verts.get(9*i+5)+ pos.z);
			
			verts.put(9*i+6, verts.get(9*i+6)+ pos.x);
			verts.put(9*i+7, verts.get(9*i+7)+ pos.y);
			verts.put(9*i+8, verts.get(9*i+8)+ pos.z);
		}
	}

}
