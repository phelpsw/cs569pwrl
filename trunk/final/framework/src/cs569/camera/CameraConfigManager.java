package cs569.camera;

import java.util.ArrayList;

public class CameraConfigManager {
	private ArrayList<CameraConfig> cameras = new ArrayList<CameraConfig>();
	int i = 0;
	
	public CameraConfigManager()
	{
		addCameraConfig(new CameraRearNarrowFOV());
		//addCameraConfig(new CameraRearWideFOV());
		addCameraConfig(new CameraOverhead());
		//addCameraConfig(new CameraHoodNarrowFOV());
		addCameraConfig(new CameraHoodWideFOV());
		//addCameraConfig(new CameraCloseRear());
		addCameraConfig(new CameraCockpit());
		addCameraConfig(new CameraWheel());
	}
	
	public CameraConfig getNextCamera()
	{
		i++;
		if(i >= cameras.size())
			i=0;
		return cameras.get(i);
	}
	
	public CameraConfig getCamera()
	{
		return cameras.get(i);
	}
	
	public CameraConfig getCamera(int index)
	{
		return cameras.get(index);
	}
	
	public void addCameraConfig(CameraConfig cam)
	{
		cameras.add(cam);
	}
}
