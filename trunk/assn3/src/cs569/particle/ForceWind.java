package cs569.particle;

import javax.vecmath.Vector3f;

public class ForceWind extends Force {
	private Vector3f dir;
	private float p_area;
	private float wind_speed;
	private float Cd; // http://en.wikipedia.org/wiki/Drag_coefficient
	
	public ForceWind(Vector3f dir, float p_area, float wind_speed, float Cd)
	{
		this.dir = dir;
		this.p_area = p_area;
		this.wind_speed = wind_speed;
		this.Cd = Cd;
	}
	
	@Override
	public Vector3f update(Vector3f pos, Vector3f velo, float mass, float time) {
		// density of air = 1.2 kg/m3 
		float pressure = (float)(0.5 * 1.2 * Math.pow(wind_speed, 2) * Cd); // units: N/m^2
		float f = pressure * p_area;
		Vector3f fwind = new Vector3f(dir);
		fwind.normalize();
		fwind.scale(f);
		return fwind;
	}

}
