package cs569.particles;

import javax.vecmath.Color4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class FireUpdater implements ParticleUpdater {

	public boolean updateParticle(float birthTime, float time, float dt, float mass,
			Vector3f position, Vector3f velocity, Color4f color,
			Object... params) {
		
		float rotationVel = (Float)params[1];
		float rotation =  (Float)params[0];
		rotation += rotationVel * dt;
		params[0] = rotation;
		
		Vector2f size = (Vector2f)params[2];
		size.x += 0.3f*dt;
		size.y += 0.3f*dt;
		
		float alphaStage = 0.3f;
		if (time - birthTime < alphaStage)
			color.w = (time - birthTime)/alphaStage;
		
		return true;
	}

}
