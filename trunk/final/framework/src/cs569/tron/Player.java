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
import cs569.material.Glow;
import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.material.Phong;
import cs569.material.Reflection;
import cs569.texture.Texture;

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
	public boolean humanCtl; // ai will be player too, don't want to update cam for ai
	Vector2f position; // current position
	Vector2f direction; // direction the player is going
	float velocity;   // distance per second
	private Wall currentWall;
	public boolean alive = true;
	private Material wallMaterial = null;
	
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
		vehicle.setPos(position);
		velocity = 80.0f;

		//TODO set camera position
		cameraCurrentTargetPosition = new Vector3f(position.x + direction.x * cameraTargetHorizontalOffset , 0.0f, position.y + direction.y * cameraTargetHorizontalOffset);
		cameraObjectiveTargetPosition = new Vector3f(cameraCurrentTargetPosition);
		camera.setTarget(cameraCurrentTargetPosition);
		cameraCurrentPosition = new Vector3f(position.x - direction.x * cameraHorizontalOffset , cameraVerticalOffset, position.y - direction.y * cameraHorizontalOffset);
		cameraObjectivePosition = new Vector3f(cameraCurrentPosition);
		camera.setEye(cameraCurrentPosition);
		
		initVehicleColor(id);
		
		// Walls must be setup after vehicle is initialized
		currentWall = new Wall(position, direction);
		currentWall.setMaterial(this.wallMaterial);		
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
			wallMaterial = new Glow(new Color3f(0.2f, 1.0f, 0.1f), new Color3f(0.2f, 1.0f, 0.1f), 1.0f, Texture.getTexture("/textures/tron/white.png"));
			break;
		case PLAYER2:
			vehicle.setBodyMaterial(new AnisotropicWard(new Color3f(1.0f, 0.2f, 0.1f),
					new Color3f(0.8f, 0.6f, 0.6f), 0.4f, 0.2f));
			vehicle.setHubMaterial(new Lambertian(new Color3f(1.0f, 1.0f, 1.0f)));
			vehicle.setWindowMaterial(new Phong(new Color3f(0.1f, 0.1f, 0.1f),new Color3f(1.0f, 1.0f, 1.0f),50.0f));
			vehicle.setWheelMaterial(new Phong(new Color3f(.5f, 0,0), new Color3f(1,1,1), 50.0f));
			wallMaterial = new Glow(new Color3f(1.0f, 0.2f, 0.1f), new Color3f(1.0f, 0.2f, 0.1f), 1.0f, Texture.getTexture("/textures/tron/white.png"));
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
		vehicle.setPos(position);		
		currentWall.setEnd(position);
		
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
		
		if (humanCtl == false)
			return;
		
		if (moveType == Player.MOVE_LEFT || moveType == Player.MOVE_RIGHT)
		{									
			float tmp;
			if (moveType == Player.MOVE_LEFT)
			{
				tmp = direction.x;
				direction.x = direction.y;
				direction.y = -tmp;
				vehicle.addRotate(QUAT_LEFT);
			} else if (moveType == Player.MOVE_RIGHT)
			{
				tmp = direction.x;
				direction.x = -direction.y;
				direction.y = tmp;
				vehicle.addRotate(QUAT_RIGHT);
			}
											
			currentWall.completeWall(position);
			currentWall = new Wall(position, direction);
			currentWall.setMaterial(this.wallMaterial);
			map.addWall(currentWall);
			
			position.x += direction.x * 5;
			position.y += direction.y * 5;
			vehicle.setPos(position);
			
			
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
