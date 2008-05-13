package cs569.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.Animator;

import cs569.animation.Animated;
import cs569.animation.Animation;
import cs569.animation.AnimationTrack;
import cs569.camera.Camera;
import cs569.material.AnisotropicWard;
import cs569.material.CookTorrance;
import cs569.material.FinishedWood;
import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.material.NormalMappedPhong;
import cs569.material.Phong;
import cs569.material.Reflection;
import cs569.material.ShadowedPhong;
import cs569.material.SkinnedPhong;
import cs569.material.SkinnedPhongProper;
import cs569.material.TexturedPhong;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.misc.OBJLoaderException;
import cs569.misc.Parser;
import cs569.misc.RotationGizmo;
import cs569.object.DefaultScene;
import cs569.object.HierarchicalObject;
import cs569.object.MeshObject;
import cs569.object.ParameterizedObjectMaker;
import cs569.object.PrimitiveFactory;
import cs569.object.Scene;
import cs569.panel.MaterialPanel;
import cs569.particles.*;
import cs569.shaders.GLSLShader;
import cs569.shaders.BloomCombineShader;
import cs569.shaders.BrightPassShader;
import cs569.shaders.GaussianBlurShader;
import cs569.texture.DynamicCubeMap;
import cs569.texture.FrameBufferObject;
import cs569.texture.HDRSceneRenderer;
import cs569.texture.PostProcessStage;
import cs569.texture.ShadowMap;
import cs569.texture.Texture;
import cs569.texture.TextureGUI;
import cs569.tron.Map;
import cs569.tron.Player;
import cs569.tron.TronParticleSystemHandler;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public class TronRuntime extends JFrame implements GLEventListener, ActionListener,
		MouseListener, MouseMotionListener, MouseWheelListener, 
		KeyListener, TreeSelectionListener, PropertyChangeListener {

	// *******************CONSTANTS************************************************************

	public static boolean TEXTON = false;
	
	// The sizes of the viewport and shadow map in pixels
	protected static final int DEFAULT_VIEWPORT_SIZE = 512;

	// The camera modes
	protected static final int CAMERA_LIGHT = 1;
	protected static final int CAMERA_MAIN = 2;

	// *******************CONSTANTS************************************************************

	// All materials that will be used must be registered here
	static final ArrayList<Class<? extends Material>> registeredMaterials = new ArrayList<Class<? extends Material>>();
	static {
		registeredMaterials.add(Lambertian.class);
		registeredMaterials.add(Phong.class);
		registeredMaterials.add(AnisotropicWard.class);
		registeredMaterials.add(CookTorrance.class);
		registeredMaterials.add(SkinnedPhong.class);
		registeredMaterials.add(SkinnedPhongProper.class);
		registeredMaterials.add(TexturedPhong.class);
		registeredMaterials.add(NormalMappedPhong.class);
		registeredMaterials.add(ShadowedPhong.class);
		registeredMaterials.add(Reflection.class);
		registeredMaterials.add(FinishedWood.class);
	}

	// The viewer for this application
	private static TronRuntime mainView;
	// The default scene assembler
	private ParameterizedObjectMaker defaultSceneMaker = new DefaultScene();
	// List of all active frame buffer objecs
	private List<FrameBufferObject> frameBufferObjects = new ArrayList<FrameBufferObject>();
	// List of all frame buffer objects needed only for HDR
	private List<FrameBufferObject> hdrFrameBufferObjects = new ArrayList<FrameBufferObject>();
	// List of all active animations
	private List<Animated> animatedObjects = new ArrayList<Animated>();
	
	
	private static final boolean sidePanelOn = false;
	
	
	private Player player1;
	private long lastTime = -1; // negative to identify first timestep
	
	
	

	// the current size of GL viewport
	protected int viewWidth = 800; //DEFAULT_VIEWPORT_SIZE;
	protected int viewHeight = 600; //DEFAULT_VIEWPORT_SIZE;

	// Current state of the GUI
	HierarchicalObject object;
	HierarchicalObject toBeLoaded = null;
	TronParticleSystemHandler particleSystemHandler = new TronParticleSystemHandler();
	protected int cameraViewMode = CAMERA_MAIN;
	protected int cameraOrbitMode = CAMERA_MAIN;
	
	// Screen filling texture
	protected Texture sfqTexture = null;

	// UI elements
	private DefaultTreeModel modelTree;
	private JTree modelTreeView;
	private JSplitPane controlPane;
	private JPanel matPanel;
	protected GLCanvas canvas;
	private Animator animator;
	protected final GLU glu = new GLU();
	private final MaterialSelectionPanel matSelectPanel = new MaterialSelectionPanel();

	/* Gizmos */
	private RotationGizmo rotationGizmo = new RotationGizmo(this);

	/* Main scene camera and a virtual camera corresponding to the light source */
	protected Camera mainCamera = new Camera();
	protected Camera lightCamera = new Camera(
			new Vector3f(0.0f, 100.0f,1.0f),
			new Vector3f(0, 0, 0), 
			new Vector3f(0, 1, 0), 70, 0.1f, 
			(float) (2 * new Vector3f(0.0f, 100.0f,1.0f).length())
	);
	
	/* HDR rendering */
	protected static boolean hdrEnabled = false;
	HDRSceneRenderer hdr;
	Texture hdrResult;

	// Turn mouse movements into view movement
	private final Point2f lastMousePoint = new Point2f();
	private final Point2f currMousePoint = new Point2f();
	protected boolean mouseDown = false;
	private long startTime = System.currentTimeMillis();

	public TronRuntime() {
		super("GigaTRON 4000XP+ Extreme Edition 4");

		if (sidePanelOn)
		{
		JPanel main = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 8.0; c.weighty = 1.0;
		c.gridx = 0; c.gridy = 0;
		main.add(createGLPanel(), c);
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1.0; c.weighty = 1.0;
		main.add(createControlPanel(), c);

		getContentPane().add(main);
		} else
		{
			getContentPane().add(createGLPanel(), BorderLayout.CENTER);
		}

		// Setup the menuBar
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		this.setJMenuBar(createMenuBar());

		// Set the frame parameteres
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Create the default scene */
		//object = defaultSceneMaker.make();
		//String filename = getClass().getResource("/scenes/mr_droopy.xml").getFile();
		//Parser parser = new Parser();
		//object = (HierarchicalObject) parser.parse(filename, Scene.class);
		object = new Map();
		
		player1 = new Player(mainCamera, Player.PLAYER1, true);
		object.addObject(player1.getCurrentWall());
		object.addObject(player1.getVehicle());
		
		// TODO: Configure better initial camera position
		mainCamera.setEye(new Vector3f(20,3,0));
		
		object.recursiveUpdateBoundingSpheres();
		
		if (sidePanelOn)
			modelTree.setRoot(object);
		
		pack();
		setVisible(true);

		/* Refresh the display */
		animator = new Animator(canvas);
		animator.setRunAsFastAsPossible(false);
		animator.start();
	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		mainView = new TronRuntime();
	}

	/**
	 * Creates the GLPanel
	 * 
	 * @return
	 */
	private JPanel createGLPanel() {
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		JPanel glPanel = new JPanel(new BorderLayout());
		glPanel.add(canvas, BorderLayout.CENTER);
		//Dimension dimen = new Dimension(DEFAULT_VIEWPORT_SIZE,
		//		DEFAULT_VIEWPORT_SIZE);
		Dimension dimen = new Dimension(viewWidth, viewHeight);
		glPanel.setPreferredSize(dimen);
		return glPanel;
	}

	/**
	 * Create the control panel on the right hand side of the viewer
	 * 
	 * @return
	 */
	private JComponent createControlPanel() {
		controlPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		// Create the tree viewer
		modelTree = new DefaultTreeModel(object);
		modelTreeView = new JTree(modelTree);
		modelTreeView.setEditable(true);
		modelTreeView.setShowsRootHandles(true);
		modelTreeView.setRootVisible(true);
		modelTreeView.addTreeSelectionListener(this);

		// Make the tree view scrollable
		JScrollPane scroll = new JScrollPane(modelTreeView,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		controlPane.setTopComponent(scroll);
		controlPane.setOneTouchExpandable(true);
		controlPane.setResizeWeight(0.5);
		controlPane.setPreferredSize(new Dimension(DEFAULT_VIEWPORT_SIZE / 2,
				DEFAULT_VIEWPORT_SIZE));

		matPanel = new JPanel(new BorderLayout());
		matPanel.add(matSelectPanel, BorderLayout.NORTH);
		controlPane.setBottomComponent(matPanel);

		return controlPane;

	}

	/**
	 * Creates the menu bar
	 * 
	 * @return
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// the file menu
		JMenu fileMenu = new JMenu("File");
		//String[] fileMenuItemNames = {"Load scene ..",  "Save scene ..", "Exit" };
		//String[] fileMenuItemActions = { "geomFile", "save", "exit" };
		//char[] fileMenuMnemonics = {'l', 's', 'x'};
		String[] fileMenuItemNames = {"Exit" };
		String[] fileMenuItemActions = {"exit" };		
		char[] fileMenuMnemonics = {'x'};
		KeyStroke[] keyStrokes = {
				//KeyStroke.getKeyStroke("control L"),
				//KeyStroke.getKeyStroke("control S"),
				KeyStroke.getKeyStroke("alt X")
		};
		addMenuItems(fileMenu, fileMenuItemNames, fileMenuItemActions,
				fileMenuMnemonics, keyStrokes
		);
		menuBar.add(fileMenu);

		/*
		
		// first the geometry menu
		JMenu geometryMenu = new JMenu("Geometry");
		geometryMenu.getPopupMenu().setLabel("Geometry");
		String[] geometryMenuItemNames = { "Default", "Load OBJ file",
				"Load OBJ file (flat)"};
		String[] geometryMenuItemActions = { "geomDefault", "geomOBJ",
				"geomOBJflat"};
		addMenuItems(geometryMenu, geometryMenuItemNames,
				geometryMenuItemActions, null, null);
		menuBar.add(geometryMenu);

		// The camera menu
		JMenu cameraMenu = new JMenu("Camera");
		cameraMenu.getPopupMenu().setLabel("Camera");
		String[] cameraMenuItemNames = { "Main", "Light" };
		String[] cameraMenuItemActions = { "SetMainCamera", "SetLightCamera" };
		char[] cameraMenuMnemonics= {'M', 'L'};
		addMenuItems(cameraMenu, cameraMenuItemNames, cameraMenuItemActions, cameraMenuMnemonics, null);
		menuBar.add(cameraMenu);

		// The orbit menu
		JMenu orbitMenu = new JMenu("Orbit");
		orbitMenu.getPopupMenu().setLabel("Orbit");
		String[] orbitMenuItemNames = { "Main", "Light" };
		String[] orbitMenuItemActions = { "OrbitMainCamera", "OrbitLightCamera" };
		char[] orbitMenuMnemonics= {'M', 'L'};
		addMenuItems(orbitMenu, orbitMenuItemNames, orbitMenuItemActions, orbitMenuMnemonics, null);
		menuBar.add(orbitMenu);

	    // The texure menu
	    JMenu textureMenu = new JMenu("Texture");
	    textureMenu.getPopupMenu().setLabel("Texture");
	    String[] textureMenuItemNames = { "Load ..", "Stop Preview" };
	    String[] textureMenuItemActions = { "LoadTexture", "StopPreview" };
		char[] textureMenuMnemonics= {'L', 'S'};
	    addMenuItems(textureMenu, textureMenuItemNames, textureMenuItemActions, textureMenuMnemonics, null);
	    menuBar.add(textureMenu);

	    // The HDR menu
	    JMenu hdrMenu = new JMenu("HDR");
	    hdrMenu.getPopupMenu().setLabel("HDR");
	    String[] hdrMenuItemNames = { "Enable HDR", "Disable HDR"};
	    String[] hdrMenuItemActions = { "EnableHDR", "DisableHDR"};
	    char[] hdrMnemonics= {'E', 'D'};
	    addMenuItems(hdrMenu, hdrMenuItemNames, hdrMenuItemActions, hdrMnemonics, null);
	    menuBar.add(hdrMenu);
	    
	    // The animation menu
	    JMenu animationMenu = new JMenu("Animation");
	    animationMenu.getPopupMenu().setLabel("Animation");
	    String[] animationMenuItemNames = { "Load ..", "Clear all" };
	    String[] animationMenuItemActions = { "LoadAnimation", "ClearAll" };
		char[] animationMenuMnemonics= {'L', 'C'};
	    addMenuItems(animationMenu, animationMenuItemNames, animationMenuItemActions, animationMenuMnemonics, null);
	    menuBar.add(animationMenu);
	    
	    // The particles menu
	    JMenu particlesMenu = new JMenu("Particles");
	    particlesMenu.getPopupMenu().setLabel("Particles");
	    String[] particlesMenuItemNames = { "Fountain", "Fire", "Swarm"};
	    String[] particlesMenuItemActions = { "Fountain", "Fire", "Swarm" };
	    addMenuItems(particlesMenu, particlesMenuItemNames, particlesMenuItemActions, null, null);
	    menuBar.add(particlesMenu);
*/

	    return menuBar;
	}

	/**
	 * Requests that the main viewer window be repainted.
	 */
	public static final void requestRepaint() {
		mainView.canvas.repaint();
	}

	/**
	 * Adds a menu item to a menu
	 * 
	 * @param menu
	 * @param names
	 * @param actions
	 */
	private void addMenuItems(JMenu menu, String[] names, String[] actions, char[] mnemonics, KeyStroke[] keyStroke) {
		for (int i = 0; i < names.length; ++i) {
			JMenuItem menuItem = new JMenuItem(names[i]);
			menuItem.setActionCommand(actions[i]);
			if (keyStroke != null && keyStroke[i] != null)
				menuItem.setAccelerator(keyStroke[i]);
			if (mnemonics != null)
				menuItem.setMnemonic(mnemonics[i]);
			menuItem.addActionListener(this);
			menu.add(menuItem);
		}
	}

	/**
	 * Sets the bottom element in the control panel
	 * 
	 * @param newBottom
	 */
	protected void setMatPanel(JComponent newBottom) {
		matPanel.removeAll();
		matPanel.add(matSelectPanel, BorderLayout.NORTH);
		if (newBottom != null)
			matPanel.add(newBottom, BorderLayout.CENTER);
		controlPane.setDividerLocation(0.55);
		matPanel.invalidate();
		controlPane.invalidate();
		matPanel.requestFocus();
		repaint();
	}

	/**
	 * Return the main viewer instance.
	 */
	public static final TronRuntime getMainViewer() {
		return mainView;
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

	/**
	 * Return the camera, which is currently used to look at the scene
	 */
	public Camera getCurrentCamera() {
		return cameraViewMode == CAMERA_LIGHT ? lightCamera : mainCamera;
	}
	
	/**
	 * Return the rotation gizmo
	 */
	public RotationGizmo getRotationGizmo() {
		return rotationGizmo;
	}
	
	/**
	 * Return the TronParticleSystemHandler
	 */
	public TronParticleSystemHandler getParticleSystemHandler()
	{
		return particleSystemHandler;
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
			if (toBeLoaded != null) {
				object = toBeLoaded;
				toBeLoaded = null;
				modelTree.setRoot(object);
			}
			
			/* Apply all animations */
			for (Animated animated : animatedObjects) {
				animated.update((System.currentTimeMillis() - startTime) / 1000.0f);
			}
			particleSystemHandler.update((System.currentTimeMillis() - startTime) / 1000.0f);

			/* Update the hierarchy of bounding spheres */
			object.recursiveUpdateBoundingSpheres();
			
			/* Do any render-to-texture operations before drawing the scene
			   to the main frame buffer */
			for (int i=0; i<frameBufferObjects.size(); i++)
				frameBufferObjects.get(i).render(gl, glu, object);
	
			/* Same for hdr-related FBOs */
			if (hdrEnabled) {
				for (int i=0; i<hdrFrameBufferObjects.size(); i++)
					hdrFrameBufferObjects.get(i).render(gl, glu, object);
			}
			
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	
			// Render the preview texture if appropriate
			if (sfqTexture != null) {
				gl.glUseProgram(0);
				sfqTexture.initializeTexture(gl);
				sfqTexture.blit(gl);
			}
	
			gamePlay();
			
			Vector3f eye = new Vector3f();
			renderCamera(gl, eye);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			if (!hdrEnabled) {
				object.glRender(gl, glu, eye);
				rotationGizmo.glRender(gl, glu, eye);
				particleSystemHandler.glRender(gl,glu,eye);
			} else {
				hdr.doToneMapping(gl, hdrResult);
			}
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
	}

	private void gamePlay()
	{
		// handle initial value
		if(lastTime < 0)
		{
			lastTime = System.currentTimeMillis();
			return;
		}
		
		float dt = System.currentTimeMillis() - lastTime;
		
		player1.update(dt);
		
		
		lastTime = System.currentTimeMillis();
	}
	
	/**
	 * Setup the camera
	 */
	private void renderCamera(GL gl, Vector3f eye) {
		if (cameraViewMode == CAMERA_LIGHT) {
			lightCamera.updateMatrices();
			eye.set(lightCamera.getEye());
			setProjectionForCamera(lightCamera, gl);
			rotationGizmo.setCamera(lightCamera);
		} else {
			mainCamera.updateMatrices();
			eye.set(mainCamera.getEye());
			setProjectionForCamera(mainCamera, gl);
			rotationGizmo.setCamera(mainCamera);
		}
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

		try {
			initializeTextures(gl);

			GLSLShader.initializeShaders(gl);

			/* Initialize all frame buffer objects */
			for (int i=0; i<frameBufferObjects.size(); i++)
				frameBufferObjects.get(i).initializeFBO(gl);
			

			for (int i=0; i<hdrFrameBufferObjects.size(); i++)
				hdrFrameBufferObjects.get(i).initializeFBO(gl);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void initializeTextures(GL gl) {
		/* Load the 'Ben's backyard' environment map 
		    - from http://www.bencloward.com/textures_extra.shtml */
		/*
		Texture backyard = Texture.createCubeMapFromFile("Backyard", 
				"/textures/cubemap/backyard_", ".png");
		backyard.initializeTexture(gl);
		*/

		/* Load the 'Grace cathedral' environment map 
		    - made by Paul Debevec */
		/*
		Texture grace = Texture.createCubeMapFromFile("Grace Cathedral", 
				"/textures/cubemap/grace_", ".png");
		grace.initializeTexture(gl);
		*/
		
		/* Load some normalmap/diffuse textures */
		Texture.getTexture("/textures/stoneBrickDiffuse.jpg").initializeTexture(gl);
		Texture.getTexture("/textures/stoneBrickNormal.jpg").initializeTexture(gl);

		/* Load parts of the 'Finished wood' data set */
		/*
		String[] woodTypes = {"cmaple", "walnut2"};
		String[] textureTypes = {"axis", "beta", "diffuse", "fiber"};
		for (String woodType: woodTypes) {
			for (String textureType: textureTypes) {
				String identifier = "/textures/wood/" + woodType + "-" + textureType + ".png";
				boolean isLinear = textureType.equals("beta") || textureType.equals("axis");
				Texture.create2DTextureFromFile(identifier, identifier, 
					GL.GL_LINEAR, GL.GL_REPEAT, true, isLinear).initializeTexture(gl);
			}
		} */

		/* A vague-looking particle from the student game 'Alpha Strain' */
		Texture.getTexture("src/textures/smoke.png").initializeTexture(gl);
		
		/* Create some frame buffer objects and attach them to the scene */
		ShadowMap shadowMap = new ShadowMap("Shadow map", lightCamera, 1024, 1024);
		frameBufferObjects.add(shadowMap);

		/* Create a dynamic cube-map generator and place it at the center of the sphere 
		   in the test scene */
		DynamicCubeMap dynamicCubeMap = new DynamicCubeMap("Dynamic cube-map", 
				new Vector3f(-0.6f, 0.3f, -0.6f), 512);
		frameBufferObjects.add(dynamicCubeMap);
		
		/* Create an hdr target for the scene */
		hdr = new HDRSceneRenderer("HDR scene renderer", this, DEFAULT_VIEWPORT_SIZE, DEFAULT_VIEWPORT_SIZE);

		/* Create fbo's in bloom chain */
		PostProcessStage brightPass = new PostProcessStage("Bloom bright-pass", FrameBufferObject.HDR_TEXTURE_FBO,
				DEFAULT_VIEWPORT_SIZE, DEFAULT_VIEWPORT_SIZE, GLSLShader.getShader(BrightPassShader.class),
				hdr, 1.0f);
		
		PostProcessStage xBlur = new PostProcessStage("X-Gaussian blur 1", FrameBufferObject.HDR_TEXTURE_FBO,
				DEFAULT_VIEWPORT_SIZE, DEFAULT_VIEWPORT_SIZE, GLSLShader.getShader(GaussianBlurShader.class),
				brightPass, 16.0f, 0);
		
		PostProcessStage yBlur = new PostProcessStage("Y-Gaussian blur 1", FrameBufferObject.HDR_TEXTURE_FBO,
				DEFAULT_VIEWPORT_SIZE, DEFAULT_VIEWPORT_SIZE, GLSLShader.getShader(GaussianBlurShader.class),
				xBlur, 16.0f, 1);
		
		PostProcessStage combine = new PostProcessStage("Bloom Combine", FrameBufferObject.HDR_TEXTURE_FBO,
				DEFAULT_VIEWPORT_SIZE, DEFAULT_VIEWPORT_SIZE, GLSLShader.getShader(BloomCombineShader.class),
				hdr, yBlur, 1.0f);
		
		hdrFrameBufferObjects.add(hdr);
		hdrFrameBufferObjects.add(brightPass);
		hdrFrameBufferObjects.add(xBlur);
		hdrFrameBufferObjects.add(yBlur);
		hdrFrameBufferObjects.add(combine);
		
		hdrResult = combine;
	}
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Java Swing action handlers
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The Handler of actions like menu selections and mouse drags. Triggered
	 * every REDRAW_PERIOD milliseconds.
	 */
	public void actionPerformed(ActionEvent e) {

		// now we handle the action events
		String ac = e.getActionCommand();

		// Action is the material selection popup
		if (ac != null && ac.equalsIgnoreCase("selMat")) {
			setMatPanel(matSelectPanel);
		}

		// Action is the main menu
		Object actionSource = e.getSource();
		String sourceType = actionSource.getClass().getName();
		if (sourceType.equalsIgnoreCase("javax.swing.JMenuItem")) {
			JMenuItem menuItem = (JMenuItem) actionSource;
			String menuName = ((JPopupMenu) (menuItem.getParent())).getLabel();
			if (menuItem.getText().equals("Load scene .."))
				geometryActions(ac);
			if (menuName != null) {	
				if (menuName.equals("Geometry")) {
					geometryActions(ac);
				} else if (menuName.equals("Camera") || menuName.equals("Orbit")) {
					cameraActions(ac);
				} else if (menuName.equals("Texture") || menuName.equals("HDR")) {
					textureActions(ac);
				} else if (menuName.equals("Animation")) {
					animationActions(ac);
				}
				return;
			}
		}

		// Action is exit
		if (ac != null && ac.equals("exit")) {
			System.exit(0);
		}

		// Action is save
		if (ac != null && ac.equals("save")) {
			JFileChooser chooser = new JFileChooser(ClassLoader.getSystemResource("").getPath());
			//chooser.setFileFilter(new FileNameExtensionFilter("CS569 Scene", "xml"));
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String filename = "";
				try {
					filename = chooser.getSelectedFile().getPath();
					PrintStream out = new PrintStream(new BufferedOutputStream(
							new FileOutputStream(filename)));
					HierarchicalObject toWrite;
					if (!(object instanceof Scene)) {
						Scene outScene = new Scene();
						outScene.addObject(object);
						toWrite = outScene;
					} else {
						toWrite = object;
					}
					toWrite.write(out, 0);
					out.close();
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(this,
							"IOException: Unable to load: " + filename + "\n" + exception);
				}
			}
		}
	}

	/** Geometry actions */
	private void geometryActions(String ac) {
		new Thread(new LoadingThread(ac, this)).start();
	}

	/** Camera Actions */
	private void cameraActions(String ac) {
		if (ac.equals("SetLightCamera")) {
			cameraViewMode = CAMERA_LIGHT;
		} else if (ac.equals("SetMainCamera")) {
			cameraViewMode = CAMERA_MAIN;
		} else if (ac.equals("OrbitLightCamera")) {
			cameraOrbitMode = CAMERA_LIGHT;
		} else if (ac.equals("OrbitMainCamera")) {
			cameraOrbitMode = CAMERA_MAIN;
		}
		canvas.repaint();
	}
	
	/** Texture Actions */
	private void textureActions(String ac) {
		if (ac.equals("LoadTexture")) {
			TextureGUI texGUI = new TextureGUI(this);
			sfqTexture = texGUI.getTexture();
		} else if (ac.equals("StopPreview")) {
			sfqTexture = null;
		} else if (ac.equals("EnableHDR")) {
			hdrEnabled = true;
		} else if (ac.equals("DisableHDR")) {
			hdrEnabled = false;
		}
	}

	/** Animation Actions */
	private void animationActions(String ac) {
		if (ac.equals("LoadAnimation")) {
			JFileChooser chooser = new JFileChooser(ClassLoader.getSystemResource("").getPath());
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String filename = chooser.getSelectedFile().getPath();
				Parser parse = new Parser();
				Animation animation = (Animation) parse.parse(filename, Animation.class);
				for (AnimationTrack<?> track : animation.getTracks())
					track.setObject(object.findByName(track.getObjectName()));
				animatedObjects.add(animation);
			}
		} else if (ac.equals("ClearAll")) {
			animatedObjects.clear();
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
			rotationGizmo.startDrag(e);
			canvas.repaint();
		}
		lastMousePoint.set(e.getX(), e.getY());
		windowToViewport(lastMousePoint);
		mouseDown = true;
	}
	
	/**
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (cameraOrbitMode == CAMERA_MAIN)
			mainCamera.dolly(e.getWheelRotation()*0.1f);
		else
			lightCamera.dolly(e.getWheelRotation()*0.1f);
		canvas.repaint();		
	}

	/**
	 * Maps a windows coordinate to a coordinate relative to the viewport
	 * 
	 * @param p
	 */
	public void windowToViewport(Tuple2f p) {
		p.set((2.0f * p.x - viewWidth) / viewWidth,
				(2.0f * (viewHeight - p.y - 1) - viewHeight) / viewHeight);
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
			rotationGizmo.drag(e);
			canvas.repaint();
			return;
		}

		if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) {
			currMousePoint.set(e.getX(), e.getY());
			windowToViewport(currMousePoint);
			if (cameraOrbitMode == CAMERA_MAIN)
				mainCamera.orbit(lastMousePoint, currMousePoint);
			else
				lightCamera.orbit(lastMousePoint, currMousePoint);
			canvas.repaint();
			lastMousePoint.set(currMousePoint);
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
		if (e.getButton() == 3) {
			rotationGizmo.endDrag(e);
			canvas.repaint();
		}
	}

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		HierarchicalObject obj = (HierarchicalObject) e.getPath()
				.getLastPathComponent();
		matSelectPanel.setForObject(obj);
		Material mat = obj.getMaterial();
		if (mat != null) {
			setMatPanel(mat.getPropertyPanel());
		}
		rotationGizmo.setObject(obj);
		canvas.repaint();
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() != MaterialPanel.MATERIAL_PANEL_UPDATE)
			return;
		HierarchicalObject obj = (HierarchicalObject) modelTreeView
				.getSelectionPath().getLastPathComponent();
		Material mat = obj.getMaterial();
		obj.setMaterial(mat);
		setMatPanel(obj.getMaterial().getPropertyPanel());
		canvas.repaint();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Unused interface methods
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {		
		switch (e.getKeyCode()) {
			//case KeyEvent.VK_DOWN: downKeyPressed = false; break;
			//case KeyEvent.VK_UP: upKeyPressed = false; break;
			case KeyEvent.VK_LEFT: player1.move(Player.MOVE_LEFT, (Map) object); break;
			case KeyEvent.VK_RIGHT: player1.move(Player.MOVE_RIGHT, (Map) object); break;
			default:
				switch(e.getKeyChar()) {
				case 'x':particleSystemHandler.explodePlayer(player1);break;
				}break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Private classes
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Thread for loading models
	 * 
	 * @author arbree Feb 2, 2006 Viewer.java 
	 * 
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class LoadingThread implements Runnable {

		/** The loading action to take */
		String action;

		/** The viewer that requested this thread. */
		TronRuntime viewer;

		public LoadingThread(String ac, TronRuntime viewer) {
			action = ac;
			this.viewer = viewer;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// set update list to default (assume no skinning unless we load a
			//   skinned mesh
			HierarchicalObject.unSetUpdateList();
			if (action.equals("geomDefault")) {
				// set the default object
				toBeLoaded = defaultSceneMaker.make();
			} else if (action.equals("geomOBJ") || action.equals("geomOBJflat")) {

				// load an OBJ file
				JFileChooser chooser = new JFileChooser(ClassLoader.getSystemResource("").getPath());
				//chooser.setFileFilter(new FileNameExtensionFilter("Wavefront object", "obj"));
				int returnVal = chooser.showOpenDialog(viewer);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String filename = "";
					try {
						filename = chooser.getSelectedFile().getPath();
						if (action.equals("geomOBJ")) {
							toBeLoaded = MeshObject.loadFromOBJwithGroups(
									filename, true);
						} else {
							toBeLoaded = MeshObject.loadFromOBJ(filename, true);
						}
					} catch (IOException exception) {
						JOptionPane.showMessageDialog(viewer,
								"IOException: Unable to load: " + filename
										+ "\n" + exception);
					} catch (OBJLoaderException exception) {
						JOptionPane.showMessageDialog(viewer,
								"ObjLoaderException: Unable to load: "
										+ filename + "\n" + exception);
					}
				}
			} else if (action.equals("geomFile")) {
				JFileChooser chooser = new JFileChooser(ClassLoader.getSystemResource("").getPath());
				//chooser.setFileFilter(new FileNameExtensionFilter("CS569 Scene", "xml"));
				int returnVal = chooser.showOpenDialog(viewer);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String filename = chooser.getSelectedFile().getPath();
					Parser parse = new Parser();
					HierarchicalObject loadObj = (HierarchicalObject) parse
							.parse(filename, Scene.class);
					loadObj.recursiveUpdateBoundingSpheres();
					/* Get a picture of the whole scene.. */
					Vector3f eye = new Vector3f(0.0f, 3.0f, -4.0f);
					eye.normalize();
					eye.scale(loadObj.getBoundingSphere().getRadius()*3f);
					mainCamera.setEye(eye);
					mainCamera.setTarget(new Vector3f(0, 0, 0));
					toBeLoaded = loadObj;
				}
			}
			//			
			// modelTree.setRoot(object);
			canvas.repaint();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					canvas.repaint();
				}				
			});
		}
	}

	/**
	 * Panel used to select the material for an object.
	 * 
	 * @author arbree Feb 1, 2006 Viewer.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class MaterialSelectionPanel extends JPanel implements
			ActionListener {
		JComboBox box = new JComboBox();

		public MaterialSelectionPanel() {
			setLayout(new FlowLayout());
			box.setPreferredSize(new Dimension(DEFAULT_VIEWPORT_SIZE / 3, 25));
			for (Class<? extends Material> element : registeredMaterials) {
				box.addItem(new ShortNameClass(element));
			}
			box.addActionListener(this);
			add(box, BorderLayout.CENTER);
		}

		/**
		 * Sets the selected material of the box for the material in the
		 * selected object.
		 * 
		 * @param inObj
		 */
		public void setForObject(HierarchicalObject inObj) {

			Material mat = inObj.getMaterial();
			Class<? extends Material> matClass = mat.getClass();
			int index = -1;
			for (Class<? extends Material> currMat : registeredMaterials) {
				index++;
				if (matClass.equals(currMat)) {
					break;
				}
			}

			if (index < 0) {
				throw new Error(
						"CS468.MaterialSelectionPanel.setForObject(): Selected object has unregistered material.");
			}

			// Set the selection but don't create an action message
			box.removeActionListener(this);
			box.setSelectedIndex(index);
			box.addActionListener(this);

		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (modelTreeView.isSelectionEmpty()) {
				JOptionPane.showMessageDialog(mainView,
						"No object selected for material assignment.");
				return;
			}

			// Get a new instance of the selected material
			Class<? extends Material> selectedClass = 
				((ShortNameClass) box.getSelectedItem()).theClass;
			Material newMat;
			try {
				newMat = selectedClass.newInstance();
			} catch (InstantiationException e1) {
				throw new Error(
						"CS468.MaterialSelectionPanel.actionPerformed(): Error instantiating material class: "
								+ selectedClass.getSimpleName());
			} catch (IllegalAccessException e1) {
				throw new Error(
						"CS468.MaterialSelectionPanel.actionPerformed(): The empty constructor for "
								+ selectedClass.getSimpleName()
								+ " is not public.");
			}

			// Get the selected object
			HierarchicalObject obj = (HierarchicalObject) modelTreeView
					.getSelectionPath().getLastPathComponent();
			obj.setMaterial(newMat);
			setMatPanel(obj.getMaterial().getPropertyPanel());
			canvas.repaint();

		}

		
		
		/**
		 * Used to store the classes with short names in the Combo box
		 * 
		 * @author arbree Feb 1, 2006 Viewer.java Copyright 2005 Program of
		 *         Computer Graphics, Cornell University
		 */
		private class ShortNameClass {
			public final Class<? extends Material> theClass;

			/**
			 * Wraps a class object
			 * 
			 * @param inClass
			 */
			public ShortNameClass(Class<? extends Material> inClass) {
				theClass = inClass;
			}

			/**
			 * Returns its simple name instead of its FQN as the Class toString
			 * does.
			 * 
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				String out = theClass.getSimpleName();
				return out;
			}
		}
	}
}
