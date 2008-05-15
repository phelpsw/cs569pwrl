package cs569.camera;

import javax.vecmath.Vector3f;

import cs569.tron.Player;

public class CameraOverhead extends CameraConfig {

	Vector3f target = new Vector3f();
	Vector3f pos = new Vector3f();
	float cameraTargetHorizontalOffset = 5.0f;
	float cameraHorizontalOffset = 1.0f;
	float cameraVerticalOffset = 30.0f;
	float FOV = 85.0f;
	
	float eyeDampeningConstant = 0.003f;
	float targetDampeningConstant = 0.01f;
	float FOVDampeningConstant = 0.00425f;
	
	public CameraOverhead()
	{
		
	}
	
	@Override
	public float getCameraFOV(Player player) {
		return FOV;
	}

	@Override
	public Vector3f getCameraPosition(Player player) {
		pos.set(player.getPosition().x - player.getDirection().x * cameraHorizontalOffset , cameraVerticalOffset, player.getPosition().y - player.getDirection().y * cameraHorizontalOffset);
		return pos;
	}

	@Override
	public Vector3f getCameraTarget(Player player) {
		target.set(player.getPosition().x, 0.0f, player.getPosition().y);
		return target;
	}
	
	@Override
	public float getEyeDampening() {
		return eyeDampeningConstant;
	}

	@Override
	public float getFOVDampening() {
		return FOVDampeningConstant;
	}

	@Override
	public float getTargetDampening() {
		return targetDampeningConstant;
	}

}
