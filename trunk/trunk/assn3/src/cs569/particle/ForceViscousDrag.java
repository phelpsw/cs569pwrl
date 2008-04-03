package cs569.particle;

import javax.vecmath.Vector3f;

public class ForceViscousDrag extends Force {
	private float k;
	
	public ForceViscousDrag(float k)
	{
		this.k = k;
	}
	@Override
	public Vector3f update(Vector3f pos, Vector3f velo, float mass, float time) {
		return new Vector3f(-k*velo.x,-k*velo.y,-k*velo.z);
	}

}
