package cs569.tron;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.camera.Camera;
import cs569.material.AnisotropicWard;
import cs569.material.Lambertian;
import cs569.material.Phong;
import cs569.material.Reflection;

public class Player {
	
	// keep track of player position
	// maintain player state
	// maintain reference to player camera and update as necessary
	
	public static final int MOVE_RIGHT=0;
	public static final int MOVE_LEFT=1;
	
	public static final int PLAYER1=0;
	public static final int PLAYER2=1;
	
	int id; // Player 1 this value == 1
	Camera camera;
	Vehicle vehicle;	
	boolean humanCtl; // ai will be player too, don't want to update cam for ai
	Vector2f position; // current position
	Vector2f direction; // direction the player is going
	float velocity;   // distance per second
	private Wall currentWall;
	private Wall lastWall;
	public boolean alive = true;
	
	Vector3f cameraCurrentPosition;
	Vector3f cameraObjectivePosition;
	Vector3f cameraCurrentTargetPosition;
	Vector3f cameraObjectiveTargetPosition;
	
	Vector3f deltaEye = new Vector3f();
	Vector3f deltaTarget = new Vector3f();
	static float eyeDampeningConstant = 0.00125f;
	static float targetDampeningConstant = 0.00425f;
	
	Vector2f temp = new Vector2f();
	static float cameraTargetHorizontalOffset = 5.0f;
	static float cameraHorizontalOffset = 15.0f;
	static float cameraVerticalOffset = 10.0f;
	
	static Quat4f QUAT_RIGHT = new Quat4f();
	static Quat4f QUAT_LEFT = new Quat4f();
	
	static {
		QUAT_RIGHT.set(new AxisAngle4f(0,1,0,(float)-Math.PI/2.0f));
		QUAT_LEFT.set(new AxisAngle4f(0,1,0,(float)Math.PI/2.0f));
	}
	
	// TODO: Add easing into position functionality for both the target and the camera position
	
	public Player(int id, boolean human)
	{
		this.camera = new Camera();
		this.humanCtl = human;		
		vehicle = new Vehicle();	
		direction = new Vector2f(0,1);
		position = new Vector2f(id*20,id*20);				
		velocity = 40.0f;
		currentWall = new Wall(position);
		lastWall = null;

		//TODO set camera position
		cameraCurrentTargetPosition = new Vector3f(position.x + direction.x * cameraTargetHorizontalOffset , 0.0f, position.y + direction.y * cameraTargetHorizontalOffset);
		cameraObjectiveTargetPosition = new Vector3f(cameraCurrentTargetPosition);
		camera.setTarget(cameraCurrentTargetPosition);
		cameraCurrentPosition = new Vector3f(position.x - direction.x * cameraHorizontalOffset , cameraVerticalOffset, position.y - direction.y * cameraHorizontalOffset);
		cameraObjectivePosition = new Vector3f(cameraCurrentPosition);
		camera.setEye(cameraCurrentPosition);
		
		
		initVehicleColor(id);
	}
	
	public Camera getCamera()
	{
		return camera;
	}
		
	public void setVisible(boolean val)
	{
		vehicle.setVisible(val);
	}
	
	private void initVehicleColor(int id)
	{
		switch(id)
		{
		case PLAYER1:
			vehicle.setBodyMaterial(new AnisotropicWard(new Color3f(0.2f, 1.0f, 0.1f),
				new Color3f(0.6f, 0.8f, 0.6f), 0.4f, 0.2f));
			vehicle.setHubMaterial(new Lambertian(new Color3f(1.0f, 1.0f, 1.0f)));
			vehicle.setWindowMaterial(new Phong(new Color3f(0.1f, 0.1f, 0.1f),new Color3f(1.0f, 1.0f, 1.0f),50.0f));
			vehicle.setWheelMaterial(new Phong(new Color3f(0,.5f,0), new Color3f(1,1,1), 50.0f));
			break;
		case PLAYER2:
			vehicle.setBodyMaterial(new AnisotropicWard(new Color3f(1.0f, 0.2f, 0.1f),
					new Color3f(0.8f, 0.6f, 0.6f), 0.4f, 0.2f));
			vehicle.setHubMaterial(new Lambertian(new Color3f(1.0f, 1.0f, 1.0f)));
			vehicle.setWindowMaterial(new Phong(new Color3f(0.1f, 0.1f, 0.1f),new Color3f(1.0f, 1.0f, 1.0f),50.0f));
			vehicle.setWheelMaterial(new Phong(new Color3f(.5f, 0,0), new Color3f(1,1,1), 50.0f));
			break;
		}		
	}
	
	
	//called every frame
	// dt is in milliseconds
	public void update(float dt)
	{
		temp.set(direction);
		temp.scale(velocity*(dt/1000.0f));
		position.add(temp);
				
		currentWall.setEnd(position);
		vehicle.setPos(position);
		
		cameraObjectiveTargetPosition.set(position.x + direction.x * cameraTargetHorizontalOffset , 0.0f, position.y + direction.y * cameraTargetHorizontalOffset);
		cameraObjectivePosition.set(position.x - direction.x * cameraHorizontalOffset , cameraVerticalOffset, position.y - direction.y * cameraHorizontalOffset);
		
		deltaEye.sub(camera.getEye(), cameraObjectivePosition);
		deltaEye.scale(-eyeDampeningConstant*dt);
		
		deltaTarget.sub(camera.getTarget(), cameraObjectiveTargetPosition);
		deltaTarget.scale(-targetDampeningConstant*dt);
		
		cameraCurrentPosition.set(camera.getEye());
		cameraCurrentPosition.add(deltaEye);
		
		cameraCurrentTargetPosition.set(camera.getTarget());
		cameraCurrentTargetPosition.add(deltaTarget);
		
		camera.setEye(cameraCurrentPosition);
		camera.setTarget(cameraCurrentTargetPosition);
	}
	
	// modify camera and vehicle positions
	public void move(int moveType, Map map)
	{		
		if (alive == false)
			return;
		
		if (moveType == Player.MOVE_LEFT || moveType == Player.MOVE_RIGHT)
		{			
			if (lastWall != null)
				lastWall.box.collidable = true; // wall trail 3 old is now collidable
			lastWall = currentWall;
			currentWall = new Wall(position);
			map.addWall(currentWall);
			
			float temp;
			if (moveType == Player.MOVE_LEFT)
			{
				temp = direction.x;
				direction.x = direction.y;
				direction.y = -temp;
				vehicle.addRotate(QUAT_LEFT);
			} else if (moveType == Player.MOVE_RIGHT)
			{
				temp = direction.x;
				direction.x = -direction.y;
				direction.y = temp;
				vehicle.addRotate(QUAT_RIGHT);
			}
			
		}
	}
	
	public Vehicle getVehicle()
	{
		return vehicle;
	}
	
	public void setCurrentWall(Wall wall)
	{
		currentWall = wall;
	}
	public Wall getCurrentWall()
	{
		return currentWall;
	}

}
