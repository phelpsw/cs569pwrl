package cs569.particles;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class PlaneCollider implements ParticleUpdater {
	Vector3f normal;
	float d;
	float coeff;
	
	public PlaneCollider(Vector3f normal, float d, float coeffRestitution) {
		this.normal = normal;
		this.normal.normalize();
		this.d = d;
		this.coeff = coeffRestitution;
	}

	public boolean updateParticle(float birthTime, float time, float dt, float mass,
			Vector3f position, Vector3f velocity, Color4f color,
			Object... params) {
		float dot = position.dot(normal);
		float veldot = velocity.dot(normal);
		if (dot < d) {
			/* Teleport particle to surface of plane */
			position.scaleAdd(d-dot, normal, position);
			
			/* Bounce the particle off the plane */
			if (veldot < 0) {
				velocity.scaleAdd(-(1+coeff)*veldot, normal, velocity);
			}
		}
		return true;
	}

}
