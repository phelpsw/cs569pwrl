package cs569.particle;

import javax.vecmath.Vector3f;

public class Particle implements Comparable {
	protected Vector3f pos;
	protected Vector3f velo;
	protected float mass;
	protected float scale;
	public static Vector3f cameraPos;
	
	protected Vector3f color;
	//protected int age;
	protected float timeBorn; // stored in seconds 
	
	public Particle(Vector3f pos, Vector3f velo, float mass, Vector3f color, float age, float scale)
	{
		this.pos = pos;
		this.velo = velo;
		this.mass = mass;
		this.color = color;
		this.timeBorn = age;
		this.scale = scale;
	}
	
	public Vector3f getPos()
	{
		return this.pos;
	}
	
	public Vector3f getVelo()
	{
		return this.velo;
	}
	
	public void updatePosVelo(Vector3f pos, Vector3f velo)
	{
		this.pos = pos;
		this.velo = velo;
	}
	
	public void set(Vector3f pos, Vector3f velo, float mass, Vector3f color, float age, float scale)
	{
		this.pos = pos;
		this.velo = velo;
		this.mass = mass;
		this.color = color;
		this.timeBorn = age;
		this.scale = scale;
	}
	
	public void set(Particle p)
	{
		this.pos = p.pos;
		this.velo = p.velo;
		this.mass = p.mass;
		this.color = p.color;
		this.timeBorn = p.timeBorn;
		this.scale = p.scale;
	}
	
	public Vector3f getColor()
	{
		return this.color;
	}

	public int compareTo(Object arg0) 
	{
		return (int)(10000.0*(((Particle)arg0).getDistance() - getDistance()));
	}
	
	public float getDistance()
	{
		Vector3f dist = new Vector3f();
		dist.sub(cameraPos, pos);
		return dist.length();
	}
	
	public float getScale()
	{
		return scale;
	}
	
}
