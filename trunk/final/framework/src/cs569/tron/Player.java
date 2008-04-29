package cs569.tron;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import cs569.camera.Camera;

public class Player {
	
	// keep track of player position
	// maintain player state
	// maintain reference to player camera and update as necessary
	
	Camera camera;
	Vehicle vehicle;
	ArrayList<Wall> walls;
	boolean humanCtl; // ai will be player too, don't want to update cam for ai
	Vector3f direction;
	float velocity;
	
	public Player(Camera camera, boolean human)
	{
		this.camera = camera;
		this.humanCtl = human;
	}
	
	public boolean checkCollisionWithWalls(Vector3f pos) // might be better to use boundingbox
	{
		return false;
	}
	
	// modify camera and vehicle positions
	public void handleKeyPress(KeyEvent e)
	{
		
	}

}
