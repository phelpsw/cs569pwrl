package cs569.particle;

import javax.vecmath.Vector3f;

public abstract class Force {
	public abstract Vector3f update(Vector3f pos, Vector3f velo, float mass, float time);
}
