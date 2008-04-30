package cs569.tron;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.camera.Camera;

public class Player {
	
	// keep track of player position
	// maintain player state
	// maintain reference to player camera and update as necessary
	
	public static final int MOVE_RIGHT=0;
	public static final int MOVE_LEFT=1;
	
	Camera camera;
	Vehicle vehicle;	
	boolean humanCtl; // ai will be player too, don't want to update cam for ai
	Vector2f position; // current position
	Vector2f direction; // direction the player is going
	float velocity;   // distance per second
	Wall currentWall;
	Vector3f cameraPosition;
	Vector3f cameraTargetPosition;
	Vector2f temp = new Vector2f();
	static float cameraTargetHorizontalOffset = 5.0f;
	static float cameraHorizontalOffset = 15.0f;
	static float cameraVerticalOffset = 10.0f;
	
	static Quat4f QUAT_RIGHT = new Quat4f();
	static Quat4f QUAT_LEFT = new Quat4f();
	
	
	static {
		QUAT_RIGHT.set(new AxisAngle4f(0,1,0,(float)Math.PI/2.0f));
		QUAT_LEFT.set(new AxisAngle4f(0,1,0,(float)-Math.PI/2.0f));
	}
	
	public Player(Camera camera, boolean human)
	{
		this.camera = camera;
		this.humanCtl = human;		
		vehicle = new Vehicle();	
		direction = new Vector2f(0,1);
		position = new Vector2f(0,0);
		velocity = 2f;
		currentWall = new Wall(position);
		
		//TODO set camera position
		cameraTargetPosition = new Vector3f(position.x + direction.x * cameraTargetHorizontalOffset , 0.0f, position.y + direction.y * cameraTargetHorizontalOffset);
		camera.setTarget(cameraTargetPosition);
		cameraPosition = new Vector3f(position.x - direction.x * cameraHorizontalOffset , cameraVerticalOffset, position.y - direction.y * cameraHorizontalOffset);
		camera.setEye(cameraPosition);
	}
	
	public boolean checkCollisionWithWalls(Vector3f pos) // might be better to use boundingbox
	{
		return false;
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
		System.out.println("vehicle position: " + position + " " + dt);
		
		cameraTargetPosition.set(position.x + direction.x * cameraTargetHorizontalOffset , 0.0f, position.y + direction.y * cameraTargetHorizontalOffset);
		camera.setTarget(cameraTargetPosition);
		cameraPosition.set(position.x - direction.x * cameraHorizontalOffset , cameraVerticalOffset, position.y - direction.y * cameraHorizontalOffset);
		camera.setEye(cameraPosition);
	}
	
	// modify camera and vehicle positions
	public void move(int moveType, Map map)
	{		
		if (moveType == Player.MOVE_LEFT || moveType == Player.MOVE_RIGHT)
		{
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
