package cs569.camera;

import javax.vecmath.Vector3f;

import cs569.tron.Player;

public abstract class CameraConfig {

	
	public CameraConfig()
	{
	}
	
	public CameraConfig(String name)
	{
		this.name = name;
	}
	
	public String name;
	
	public abstract Vector3f getCameraTarget(Player player);
	public abstract Vector3f getCameraPosition(Player player);
	public abstract float getCameraFOV(Player player);
	
	public abstract float getEyeDampening();
	public abstract float getTargetDampening();
	public abstract float getFOVDampening();
	
	public String toString()
	{
		return name;
	}
}
