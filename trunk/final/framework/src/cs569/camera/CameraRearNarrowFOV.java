package cs569.camera;

import javax.vecmath.Vector3f;

import cs569.tron.Player;

public class CameraRearNarrowFOV extends CameraConfig {

	Vector3f target = new Vector3f();
	Vector3f pos = new Vector3f();
	float cameraTargetHorizontalOffset = 5.0f;
	float cameraHorizontalOffset = 15.0f;
	float cameraVerticalOffset = 10.0f;
	float FOV = 45.0f;
	
	public CameraRearNarrowFOV()
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
		target.set(player.getPosition().x + player.getDirection().x * cameraTargetHorizontalOffset , 0.0f, player.getPosition().y + player.getDirection().y * cameraTargetHorizontalOffset);
		return target;
	}

}
