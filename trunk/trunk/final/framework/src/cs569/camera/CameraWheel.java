package cs569.camera;

import javax.vecmath.Vector3f;

import cs569.tron.Player;

public class CameraWheel extends CameraConfig {

	Vector3f target = new Vector3f();
	Vector3f pos = new Vector3f();
	float cameraTargetHorizontalOffset = 10.0f;
	float cameraSideOffset = 7.0f;
	float cameraForwardOffset = 10.0f;
	float cameraVerticalOffset = 0.5f;
	float FOV = 60.0f;
	
	float eyeDampeningConstant = 0.01f;
	float targetDampeningConstant = 0.01f;
	float FOVDampeningConstant = 0.00425f;
	
	public CameraWheel()
	{
		super("Wheel");
	}
	
	@Override
	public float getCameraFOV(Player player) {
		return FOV;
	}

	@Override
	public Vector3f getCameraPosition(Player player) {
		if(player.getDirection().x > 0) // posx
			pos.set(player.getPosition().x + cameraForwardOffset, cameraVerticalOffset, player.getPosition().y - cameraSideOffset);
		else if(player.getDirection().y > 0) // posy
			pos.set(player.getPosition().x + cameraSideOffset, cameraVerticalOffset, player.getPosition().y + cameraForwardOffset);
		else if(player.getDirection().x < 0) // negx
			pos.set(player.getPosition().x - cameraForwardOffset, cameraVerticalOffset, player.getPosition().y + cameraSideOffset);
		else if(player.getDirection().y < 0) // negy
			pos.set(player.getPosition().x - cameraSideOffset, cameraVerticalOffset, player.getPosition().y - cameraForwardOffset);
		
		return pos;
	}

	@Override
	public Vector3f getCameraTarget(Player player) {
		target.set(player.getPosition().x + player.getDirection().x * cameraTargetHorizontalOffset , 0.0f, player.getPosition().y + player.getDirection().y * cameraTargetHorizontalOffset);
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
