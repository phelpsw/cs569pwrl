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
	
	static Quat4f QUAT_RIGHT = new Quat4f();
	static Quat4f QUAT_LEFT = new Quat4f();
	
	static {
		QUAT_RIGHT.set(new AxisAngle4f(0,1,0,(float)-Math.PI/2.0f));
		QUAT_LEFT.set(new AxisAngle4f(0,1,0,(float)Math.PI/2.0f));
	}
	
	public Player(Camera camera, boolean human)
	{
		this.camera = camera;
		this.humanCtl = human;		
		
	}
	
	public boolean checkCollisionWithWalls(Vector3f pos) // might be better to use boundingbox
	{
		return false;
	}
	
	//called every frame
	public void update(float dt)
	{
		Vector2f temp = new Vector2f(direction);
		temp.scale(velocity*dt);
		position.add(temp);
		
		currentWall.setEnd(position);
		vehicle.setPos(position);
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
				direction.x = -direction.y;
				direction.y = temp;
				vehicle.addRotate(QUAT_LEFT);
			} else if (moveType == Player.MOVE_RIGHT)
			{
				temp = direction.x;
				direction.x = direction.y;
				direction.y = -temp;
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

}
