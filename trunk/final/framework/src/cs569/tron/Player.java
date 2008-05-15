package cs569.tron;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.tron.TronParticleSystemHandler;
import cs569.apps.TronRuntime;
import cs569.camera.Camera;
import cs569.camera.CameraConfigManager;
import cs569.material.AnisotropicWard;
import cs569.material.Glow;
import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.material.Phong;
import cs569.material.Reflection;
import cs569.misc.BoundingBox;
import cs569.misc.GLSLErrorException;
import cs569.object.Group;
import cs569.texture.Texture;

public class Player {
	
	// keep track of player position
	// maintain player state
	// maintain reference to player camera and update as necessary
	
	public static final int MOVE_RIGHT=0;
	public static final int MOVE_LEFT=1;
	public static final int NEXT_CAMERA=2;
	
	public static final int ALIVE=0;
	public static final int DEAD=1;
	public static final int DYING=2;
	
	public static final int PLAYER1=0;
	public static final int PLAYER2=1;
	
	int id; // Player 1 this value == 0
	Camera camera;
	Vehicle vehicle;	
	public boolean humanCtl; // ai will be player too, don't want to update cam for ai
	Vector2f position; // current position
	Vector2f direction; // direction the player is going
	float velocity;   // distance per second
	private Wall currentWall;
	private ArrayList<Wall> mywalls = new ArrayList<Wall>();
	private int state;
	private Material wallMaterial = null;
	
	Vector3f cameraCurrentPosition;
	Vector3f cameraObjectivePosition;
	Vector3f cameraCurrentTargetPosition;
	Vector3f cameraObjectiveTargetPosition;
	float cameraObjectiveFOV;
	float cameraCurrentFOV;
	
	// Length of death in ms
	static float deathSequencePeriod = 2500.0f;
	float deathSequencePosition = 0f;
	Vector3f deathWallScale = new Vector3f();
	Group deathTriangles;
	
	float lastTimeUpdated = -1.0f;
	
	TronParticleSystemHandler particleSystemHandler;
	
	CameraConfigManager camman = new CameraConfigManager();
	
	BoundingBox aiBoundingBox = null;
	static float aiLookAheadHorizontalOffsetMin = 0.0f;
	static float aiLookAheadHorizontalOffsetMax = 10.0f;
	Vector3f aiLookAheadMin = new Vector3f();
	Vector3f aiLookAheadMax = new Vector3f();
	
	Random rand = new Random();
	
	Vector3f deltaEye = new Vector3f();
	Vector3f deltaTarget = new Vector3f();
	float deltaFOV = 0;
	
	
	Vector2f temp = new Vector2f();
	
	static Quat4f QUAT_RIGHT = new Quat4f();
	static Quat4f QUAT_LEFT = new Quat4f();
	
	static {
		QUAT_RIGHT.set(new AxisAngle4f(0,1,0,(float)-Math.PI/2.0f));
		QUAT_LEFT.set(new AxisAngle4f(0,1,0,(float)Math.PI/2.0f));
	}
	
	public Player(int id, boolean human)
	{
		this.camera = new Camera();
		this.humanCtl = human;	
		this.id = id;
		direction = new Vector2f();
		position = new Vector2f();		

		cameraCurrentTargetPosition = new Vector3f();
		cameraObjectiveTargetPosition = new Vector3f();		
		cameraCurrentPosition = new Vector3f();
		cameraObjectivePosition = new Vector3f();
		cameraObjectiveFOV = 0;
		cameraCurrentFOV = 0;
		
		resetPlayer();
	}
	
	public void resetPlayer()
	{
		state = Player.ALIVE;
		velocity = 80.0f;
		vehicle = new Vehicle();
		initVehicleColor(id);
		direction.set(0,1);
		position.set(id*20,id*20);
		vehicle.setPos(position);
		
		cameraCurrentTargetPosition.set(camman.getCamera().getCameraTarget(this));
		cameraObjectiveTargetPosition.set(cameraCurrentTargetPosition);
		camera.setTarget(cameraCurrentTargetPosition);
		cameraCurrentPosition.set(camman.getCamera().getCameraPosition(this));
		cameraObjectivePosition.set(cameraCurrentPosition);
		camera.setEye(cameraCurrentPosition);
		cameraCurrentFOV = camman.getCamera().getCameraFOV(this);
		cameraObjectiveFOV = cameraCurrentFOV;
		camera.setYFOV(cameraCurrentFOV);

		// Walls must be setup after vehicle is initialized
		currentWall = new Wall(position, direction);
		currentWall.setMaterial(this.wallMaterial);
		mywalls.clear();
		mywalls.add(currentWall);
		
		deathTriangles = new Group();
		((Map)TronRuntime.getRootObject()).addObject(deathTriangles);
		particleSystemHandler = new TronParticleSystemHandler(this, deathTriangles);
		
		// perform AI operation if AI player
		if(humanCtl == false)
		{
			aiBoundingBox = new BoundingBox();
		}
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public void destroy()
	{
		cameraObjectiveTargetPosition.set(camman.getCamera("RearWideFOV").getCameraTarget(this));
		cameraObjectivePosition.set(camman.getCamera("RearWideFOV").getCameraPosition(this));
		cameraObjectiveFOV = camman.getCamera("RearWideFOV").getCameraFOV(this);
		
		velocity = 0.0f;
		state = Player.DYING;
		vehicle.removeFromParent();
		deathSequencePosition = 0.0f;
		particleSystemHandler.explodePlayer(this);
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
	
	
	public void scaleMyWalls(Vector3f scale)
	{
		for(int i=0; i<mywalls.size(); i++)
		{
			mywalls.get(i).setScale(scale);
		}
	}
	
	//called every frame
	public void update(float time)
	{
		particleSystemHandler.update(time);
		
		if (lastTimeUpdated < 0) {
			lastTimeUpdated = time;
			return;
		}
		// dt is in ms
		float dt = (time - lastTimeUpdated)*1000.0f;
		
		if(state == Player.DYING)
		{
			deathSequencePosition += dt;
			float deathSequenceRatio = deathSequencePosition / deathSequencePeriod;
			deathWallScale.set(1, (1.0f - deathSequenceRatio), 1);
			scaleMyWalls(deathWallScale);
			if(deathSequenceRatio > 1.0)
			{
				for(int i=0; i<mywalls.size(); i++)
				{
					mywalls.get(i).removeFromParent();
				}
				
				deathTriangles.removeFromParent();
				state = Player.DEAD;
				return;
			}
		}
		
		temp.set(direction);
		temp.scale(velocity*(dt/1000.0f));
		position.add(temp);					
		vehicle.setPos(position);		
		currentWall.setEnd(position);
		
		cameraObjectiveTargetPosition.set(camman.getCamera().getCameraTarget(this));
		cameraObjectivePosition.set(camman.getCamera().getCameraPosition(this));
		cameraObjectiveFOV = camman.getCamera().getCameraFOV(this);
		
		deltaEye.sub(camera.getEye(), cameraObjectivePosition);
		deltaEye.scale(-camman.getCamera().getEyeDampening()*dt);
		
		deltaTarget.sub(camera.getTarget(), cameraObjectiveTargetPosition);
		deltaTarget.scale(-camman.getCamera().getTargetDampening()*dt);
		
		deltaFOV = camera.getYFOV() - cameraObjectiveFOV;
		deltaFOV *= -camman.getCamera().getFOVDampening()*dt;
		
		cameraCurrentPosition.set(camera.getEye());
		cameraCurrentPosition.add(deltaEye);
		
		cameraCurrentTargetPosition.set(camera.getTarget());
		cameraCurrentTargetPosition.add(deltaTarget);
		
		cameraCurrentFOV = camera.getYFOV() + deltaFOV;
		
		camera.setEye(cameraCurrentPosition);
		camera.setTarget(cameraCurrentTargetPosition);
		camera.setYFOV(cameraCurrentFOV);
		
		// perform AI operation if AI player
		if(humanCtl == false)
		{
			
			aiBoundingBox.reset();
			aiLookAheadMin.set(position.x + direction.x * aiLookAheadHorizontalOffsetMin , 0.0f, position.y + direction.y * aiLookAheadHorizontalOffsetMin);
			aiLookAheadMax.set(position.x + direction.x * aiLookAheadHorizontalOffsetMax , 20.0f, position.y + direction.y * aiLookAheadHorizontalOffsetMax);
			aiBoundingBox.expandBy(aiLookAheadMin);
			aiBoundingBox.expandBy(aiLookAheadMax);
			
			if(TronRuntime.getRootObject().recursiveCheckCollision(aiBoundingBox))
			{
				if(rand.nextBoolean())
				{
					move(MOVE_RIGHT);
				} else {
					move(MOVE_LEFT);
				}
				System.out.println("collide");
			}
		}
		
		lastTimeUpdated = time;
	}
	
	// modify camera and vehicle positions
	public void move(int moveType)
	{
		if(state == Player.DEAD)
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
			((Map)TronRuntime.getRootObject()).addWall(currentWall);
			mywalls.add(currentWall);
			
			position.x += direction.x * 5;
			position.y += direction.y * 5;
			vehicle.setPos(position);	
		} 
		else if(moveType == NEXT_CAMERA)
		{
			camman.getNextCamera();
		}
	}
	
	public void renderParticles(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException
	{
		particleSystemHandler.glRender(gl, glu, eye);
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
	public Vector2f getPosition()
	{
		return position;
	}
	
	public Vector2f getDirection()
	{
		return direction;
	}
	public int getState()
	{
		return state;
	}

}
