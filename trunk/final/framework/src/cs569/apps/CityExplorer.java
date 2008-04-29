package cs569.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.Animator;

import cs569.camera.Camera;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.misc.Parser;
import cs569.object.HierarchicalObject;
import cs569.object.Scene;
import cs569.shaders.GLSLShader;

/**
 * Interactive city explorer -- uses the city of Vienna data set by 
 * Peter Wonka and Michael Wimmer
 * (http://www.cg.tuwien.ac.at/research/vr/urbanmodels/index.html)
 * 
 * Created on April 13, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class CityExplorer extends JFrame implements GLEventListener, 
	MouseMotionListener, KeyListener {
	protected int viewWidth = 800;
	protected int viewHeight = 600;

	protected HierarchicalObject object;
	protected Point lastMouseLoc;
	protected boolean downKeyPressed, upKeyPressed, 
		leftKeyPressed, rightKeyPressed;
	protected static CityExplorer explorer;
	protected int framesDrawn = 0, fps = 5;
	protected long lastFPSTime = System.currentTimeMillis();
	protected long lastFrameTime = System.currentTimeMillis();
	protected Vector3f lightPos = new Vector3f();

	// UI elements
	protected GLCanvas canvas;
	private Animator animator;
	protected final GLU glu = new GLU();

	/* Main scene camera and a virtual camera corresponding to the light source */
	protected Camera mainCamera = new Camera();
	protected Camera overviewCamera = new Camera();
	protected Camera lightCamera = new Camera(
			new Vector3f(0.0f, 3.0f,-4.0f),
			new Vector3f(0, 0, 0), 
			new Vector3f(0, 1, 0), 45, 0.1f, 
			(float) (2 * new Vector3f(0.0f, 3.0f,-4.0f).length())
	);
	protected boolean showOverview = false;

	public CityExplorer() {
		super("City Explorer");

		getContentPane().add(createGLPanel(), BorderLayout.CENTER);
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Create the default scene */
		Parser parser = new Parser();

		/* Optional: replace with "cityUntextured.xml" or "citySimple.xml"
		   to reduce loading times + memory requirements*/
		object = (HierarchicalObject) parser.parse(getClass().getResource(
				"/scenes/city/cityTextured.xml").getFile(), Scene.class);

		/* Load additional parts and merge them into the city scene */
		String otherParts[] = { "/scenes/city/plane.xml", "/scenes/city/roads.xml",
				"/scenes/city/roofs.xml"
		};
		for (int i=0; i<otherParts.length; i++) {
			HierarchicalObject obj = ((HierarchicalObject) parser.parse(
				getClass().getResource(otherParts[i]).getFile(), Scene.class));
			for (int j=0; j<obj.getChildCount(); j++)
				object.addObject((HierarchicalObject) obj.getChildAt(j));
		}

		object.recursiveUpdateBoundingSpheres();

		/* Start with a good view position */
		float aspect = (float) viewWidth / viewHeight;
		mainCamera.setEye(new Vector3f(65.3f, 18.5f, -25.7f));
		mainCamera.setTarget(new Vector3f(71.5f, 18.5f, -27.8f));
		overviewCamera.setEye(new Vector3f(85.2f, 254.9f, -74.1f));
		overviewCamera.setTarget(new Vector3f(85.2f, 253.9f, -74.0f));

		mainCamera.setAspect(aspect);
		mainCamera.setYFOV(45.0f/aspect);
		overviewCamera.setAspect(aspect);
		overviewCamera.setYFOV(45.0f/aspect);

		pack();
		setVisible(true);

		/* Refresh the display */
		animator = new Animator(canvas);
		animator.setRunAsFastAsPossible(false);
		animator.start();
	}

	public static void main(String[] args) {
		 explorer = new CityExplorer();
	}
	
	public static CityExplorer getCityExplorer() {
		return explorer;
	}

	/**
	 * Creates the GLPanel
	 * 
	 * @return
	 */
	private JPanel createGLPanel() {
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addMouseMotionListener(this);
		JPanel glPanel = new JPanel(new BorderLayout());
		glPanel.add(canvas, BorderLayout.CENTER);
		Dimension dimen = new Dimension(viewWidth,
				viewHeight);
		glPanel.setPreferredSize(dimen);
		return glPanel;
	}
	
	/**
	 * Return the 'View' camera
	 */
	public Camera getViewCamera() {
		return mainCamera;
	}

	/**
	 * Return the camera corresponding to the light source
	 */
	public Camera getLightCamera() {
		return lightCamera;
	}
	
	/**
	 * Return the world-space light source position
	 */
	public Vector3f getLightPosition() {
		return lightCamera.getEye();
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Display methods
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Setup for rendering with a given camera
	 */
	private void setProjectionForCamera(Camera inCam, GL gl) {
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMultMatrixf(GLUtils.fromMatrix4f(inCam.getProjectionMatrix()), 0);
		gl.glMultMatrixf(GLUtils.fromMatrix4f(inCam.getViewMatrix()), 0);
	}

	/**
	 * @see net.java.games.jogl.GLEventListener#display(net.java.games.jogl.GLDrawable)
	 */
	public void display(GLAutoDrawable gLDrawable) {
		final GL gl = gLDrawable.getGL();

		// Render
		try {
			gl.glClearColor(65/255.0f, 91/255.0f, 124/255.0f, 1.0f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			Vector3f eye = new Vector3f();
			renderCamera(gl, eye);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			object.glRender(gl, glu, eye);
		} catch (GLSLErrorException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"GLSLErrorException: " + e.getMessage());
			System.exit(-1);
		}

		int glError = gl.glGetError();
		if (glError != 0) {
			String errorString = glu.gluErrorString(glError);
			System.out.println("OpenGL Error: " + errorString);
			System.exit(-1);
		}

		long currentTime = System.currentTimeMillis();
		float speed = (currentTime-lastFrameTime) / 1000.0f;
		if (downKeyPressed)
			mainCamera.dolly(3.0f * speed);
		if (upKeyPressed)
			mainCamera.dolly(-3.0f * speed);
		if (leftKeyPressed)
			mainCamera.strafe(-1.0f * speed);
		if (rightKeyPressed)
			mainCamera.strafe(1.0f * speed);
		lastFrameTime = currentTime;

		/* Keep an FPS counter */
		if (currentTime - lastFPSTime > 1000) {
			lastFPSTime = currentTime;
			fps = framesDrawn;
			framesDrawn = 0;
			setTitle("City Explorer (" + fps + " fps)");
		}
		framesDrawn++;
		
	}

	/**
	 * Setup the camera
	 */
	private void renderCamera(GL gl, Vector3f eye) {
		/* Boring light at the eye position. */
		lightPos.set(0.0f, 0.1f, 0.0f);
		lightPos.add(mainCamera.getEye());
		lightCamera.setEye(lightPos);
		mainCamera.updateMatrices();
		overviewCamera.updateMatrices();
		eye.set(mainCamera.getEye());
		if (showOverview)
			setProjectionForCamera(overviewCamera, gl);
		else
			setProjectionForCamera(mainCamera, gl);
	}

	/**
	 * @see net.java.games.jogl.GLEventListener#displayChanged(net.java.games.jogl.GLDrawable,
	 *      boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable,
	 *      int, int, int, int)
	 */
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		final GL gl = gLDrawable.getGL();

		viewWidth = width;
		viewHeight = height;

		gl.glLoadIdentity();
		gl.glViewport(0, 0, width, height);

		float aspect = (float) width / height;
		mainCamera.setAspect(aspect);
		mainCamera.setYFOV(45.0f / aspect);
		overviewCamera.setAspect(aspect);
		overviewCamera.setYFOV(45.0f / aspect);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Initialization Methods
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialization code. Will set all of the constants for OpenGL
	 */
	public void init(GLAutoDrawable gLDrawable) {
		final GL gl = gLDrawable.getGL();

		// Initialize the drawing surfaces
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		gl.glClearDepth(1.0f);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
		canvas.addKeyListener(this);
		addKeyListener(this);

		try {
			GLSLShader.initializeShaders(gl);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void windowToViewport(Tuple2f p) {
		p.set((2.0f * p.x - viewWidth) / viewWidth,
				(2.0f * (viewHeight - p.y - 1) - viewHeight) / viewHeight);
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN: downKeyPressed = true; break;
			case KeyEvent.VK_UP: upKeyPressed = true; break;
			case KeyEvent.VK_LEFT: leftKeyPressed = true; break;
			case KeyEvent.VK_RIGHT: rightKeyPressed = true; break;
			case KeyEvent.VK_O: showOverview = !showOverview; break;
		}
	}

	public void keyReleased(KeyEvent e) {		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN: downKeyPressed = false; break;
			case KeyEvent.VK_UP: upKeyPressed = false; break;
			case KeyEvent.VK_LEFT: leftKeyPressed = false; break;
			case KeyEvent.VK_RIGHT: rightKeyPressed = false; break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		float xDelta = (e.getX() - (float) lastMouseLoc.getX()) * 0.002f;
		float yDelta = (e.getY() - (float) lastMouseLoc.getY()) * 0.0005f; 
		mainCamera.yaw(xDelta);
		mainCamera.pitch(-yDelta);
		lastMouseLoc = e.getPoint();
	}

	public void mouseMoved(MouseEvent e) {
		lastMouseLoc = e.getPoint();
	}
}