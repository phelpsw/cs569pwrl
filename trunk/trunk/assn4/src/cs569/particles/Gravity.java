package cs569.particles;

import javax.vecmath.Vector3f;

public class Gravity implements Force {

	Vector3f gravity;
	
	public Gravity(Vector3f gravity) {
		this.gravity = gravity;
	}
	
	public void evaluate(Vector3f outForce, Vector3f position, Vector3f velocity, float mass,
			Object... params) {
		outForce.set(gravity);
		outForce.scale(mass);
	}
}
