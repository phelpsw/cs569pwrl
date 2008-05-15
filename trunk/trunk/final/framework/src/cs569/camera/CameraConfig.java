package cs569.camera;

import javax.vecmath.Vector3f;

import cs569.tron.Player;

public abstract class CameraConfig {

	public CameraConfig()
	{
		
	}
	
	public abstract Vector3f getCameraTarget(Player player);
	public abstract Vector3f getCameraPosition(Player player);
	public abstract float getCameraFOV(Player player);
	
}
