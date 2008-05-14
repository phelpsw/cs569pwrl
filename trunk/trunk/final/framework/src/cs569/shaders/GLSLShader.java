/*
 * Created on Jan 25, 2007
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package cs569.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.media.opengl.GL;

import cs569.misc.GLSLErrorException;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public abstract class GLSLShader {
	// //////////////////////////////////////////////////////////////////////
	// Shader registry --
	//
	// All shaders are registered. There shader be only only shader per class
	// and the registry keeps a map from shader classes to their singleton
	// shader
	// instances. The shader instance for a particular class can be retrieved
	// from the map with the getShader(Class class) method. Shaders can be
	// registered with the registerShader(Class, GLSLShader) method. It is
	// recommended that you do this in a static block in each shader class
	// (see Lambertian).
	// //////////////////////////////////////////////////////////////////////

	// The registry map
	private static final HashMap<Class<? extends GLSLShader>, GLSLShader> SHADER_REGISTRY;

	// Register shaders for later use
	static {
		SHADER_REGISTRY = new HashMap<Class<? extends GLSLShader>, GLSLShader>();
		registerShader(LambertianShader.class, new LambertianShader());
		registerShader(PhongShader.class, new PhongShader());
		registerShader(CookTorranceShader.class, new CookTorranceShader());
		registerShader(AnisotropicWardShader.class, new AnisotropicWardShader());
		registerShader(SkinnedPhongShader.class, new SkinnedPhongShader());
		registerShader(SkinnedPhongProperShader.class, new SkinnedPhongProperShader());
		registerShader(ReflectionShader.class, new ReflectionShader());
		registerShader(ShadowedPhongShader.class, new ShadowedPhongShader());
		registerShader(TexturedPhongShader.class, new TexturedPhongShader());
		registerShader(NormalMappedPhongShader.class, new NormalMappedPhongShader());
		registerShader(FinishedWoodShader.class, new FinishedWoodShader());
		registerShader(ToneMappingShader.class, new ToneMappingShader());
		registerShader(BrightPassShader.class, new BrightPassShader());
		registerShader(GaussianBlurShader.class, new GaussianBlurShader());
		registerShader(BloomCombineShader.class, new BloomCombineShader());
		registerShader(CityShader.class, new CityShader());
		registerShader(GlowShader.class, new GlowShader());
		registerShader(ShadowedGlowShader.class, new ShadowedGlowShader());
	}

	/**
	 * Returns the shader for a particular class.
	 */
	public static final GLSLShader getShader(
			Class<? extends GLSLShader> requestedClass) {
		GLSLShader outShader = SHADER_REGISTRY.get(requestedClass);
		if (outShader != null) {
			return outShader;
		}

		throw new Error("Class is not registered as a shader."
				+ "Did you register the shader class in GLSLShader?");
	}

	/**
	 * Register a single shader instance for a particular class.
	 * 
	 * @param classToRegister
	 * @param shaderInstance
	 */
	public static final void registerShader(
			Class<? extends GLSLShader> classToRegister,
			GLSLShader shaderInstance) {
		if (SHADER_REGISTRY.containsKey(classToRegister)) {
			throw new Error("Shader already regitered for: "
					+ classToRegister.getSimpleName());
		}
		SHADER_REGISTRY.put(classToRegister, shaderInstance);
	}

	/**
	 * Load all the registered shaders
	 */
	public static final void initializeShaders(GL gl) throws IOException,
			GLSLErrorException {
		for (GLSLShader s : SHADER_REGISTRY.values()) {
			s.loadShader(gl);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////

	// Default directory to look for shaders
	private static final String SHADER_PREFIX = "shaders/";

	// Used to create default shader names
	private static final String VERTEX_FILENAME_SUFFIX = ".vp";
	private static final String FRAGMENT_FILENAME_SUFFIX = ".fp";

	/** The names of the files containing the shader code */
	protected final String vertexShaderFileName;
	protected final String fragmentShaderFileName;

	/** The vertex and fragment shader handles */
	protected int vertexShader;
	protected int fragmentShader;

	/* The combined GPU program handle */
	protected int program;

	/**
	 * Convenience constructor that assumes that the shader filenames and entry
	 * points are uniformly named.
	 * 
	 * @param inBaseName
	 */
	protected GLSLShader(String inBaseName) {
		this(inBaseName + VERTEX_FILENAME_SUFFIX, inBaseName
				+ FRAGMENT_FILENAME_SUFFIX);
	}

	/**
	 * Initialize the shader names
	 * 
	 * @param inVertexShaderName
	 * @param inFragmentShaderName
	 */
	protected GLSLShader(String inVertexShaderName, String inFragmentShaderName) {
		if (inVertexShaderName == null || inFragmentShaderName == null) {
			throw new Error(this.getClass().getName()
					+ ": Shader filenames cannot be null.");
		}
		vertexShaderFileName = inVertexShaderName;
		fragmentShaderFileName = inFragmentShaderName;
	}

	/**
	 * Disables the use of all shaders
	 */
	public final static void disableShaders(GL gl) {
		gl.glUseProgram(0);
	}

	/**
	 * compileAndLoadShader() compiles and loads the appropriate GLSL program for
	 * the shader
	 */
	protected static int compileAndLoadShader(GL gl, int type, String filename)
			throws IOException, GLSLErrorException {
		int shader = gl.glCreateShader(type);

		if (cs569.apps.TronRuntime.TEXTON)
			System.out.println("Compiling '" + filename + "'");
		URL url = GLSLShader.class.getClassLoader().getResource(SHADER_PREFIX + filename);

		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line, contents = "";
		while ((line = reader.readLine()) != null)
			contents += line + "\n";
		gl.glShaderSource(shader, 1, new String[] { contents },
				new int[] { contents.length() }, 0);
		gl.glCompileShader(shader);
		reader.close();
		GLSLErrorException.checkError(gl, shader);
		return shader;

	}

	/**
	 * Compiles the shader code and loads it into memory
	 */
	protected void loadShader(GL gl) throws IOException, GLSLErrorException {
		// Compile and load the shader programs
		vertexShader = compileAndLoadShader(gl, GL.GL_VERTEX_SHADER,
				vertexShaderFileName);
		fragmentShader = compileAndLoadShader(gl, GL.GL_FRAGMENT_SHADER,
				fragmentShaderFileName);

		if (cs569.apps.TronRuntime.TEXTON)
			System.out.println("Linking ..");
		program = gl.glCreateProgram();
		gl.glAttachShader(program, vertexShader);
		gl.glAttachShader(program, fragmentShader);
		gl.glLinkProgram(program);
		GLSLErrorException.checkError(gl, program);
		gl.glValidateProgram(program);
		GLSLErrorException.checkError(gl, program);

		// Get the parameters
		retrieveGLSLParams(gl);
	}

	protected int getNamedParameter(GL gl, String name)
			throws GLSLErrorException {
		int location = gl.glGetUniformLocation(program, name);
		if (location == -1)
		{
			if (cs569.apps.TronRuntime.TEXTON)
				System.out.println("Warning: Parameter '" + name + "' not found!");
		}
		return location;
	}

	/**
	 * Binds the shader for immediate use.
	 */
	public void bindShader(GL gl) {
		gl.glUseProgram(program);
	}

	/**
	 * Retrieve all the needed parameter handles from the compiled shaders.
	 */
	protected abstract void retrieveGLSLParams(GL gl) throws GLSLErrorException;

	/**
	 * Set the parameters for this shader
	 * 
	 * @param params
	 */
	public abstract void setGLSLParams(GL gl, Object... params);
}
