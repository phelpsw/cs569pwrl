package cs569.particle;

import javax.vecmath.Vector3f;

public class ForceGravity extends Force {

	@Override
	public Vector3f update(Vector3f pos, Vector3f velo, float mass, float time) {
		return new Vector3f(0.0f,-9.80665f * mass,0.0f);
	}

}
