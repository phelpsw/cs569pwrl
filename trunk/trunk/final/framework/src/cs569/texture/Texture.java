/*
 * Created on Feb 10, 2006
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package cs569.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * A class for loading and managing textures
 *
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public class Texture {
	/** A string identifier that uniquely identifies this texture * */
	protected String identifier;

	/** Texture mode: GL_TEXTURE_1D, GL_TEXTURE_2D, .. */
	protected int mode;

	/** Texture filtering mode: GL_NEAREST, GL_LINEAR, .. */
	protected int filterMode;
	
	/** Texture wrapping mode: GL_REPEAT, GL_CLAMP, .. */
	protected int wrapMode;
		
	/** Internal texture format: GL_RGB, GL_RGB8, .. */
	protected int internalFormat;

	/** Texture format: GL_RGB, GL_RGB8, .. */
	protected int textureFormat;

	/** Underlying data format: GL_UNSIGNED_BYTE, GL_INT .. */
	protected int dataFormat;
	
	/** Is mip-mapping being used? */
	protected boolean mipMapOn;

	/** The size of the texture */
	protected int width, height;

	/** The data for the texture */
	protected Buffer[] data;

	/** The GL ID of the texture */
	protected int textureID;

	/** Flag indicating whether the texture has been initialized */
	protected boolean initialized = false, initializing = false;

	/**
	 * Create a texture object. The parameters should all be GL constants.
	 * 
	 * @param identifier A unique identifier for this texture
	 * @param mode (GL_TEXTURE_1D, GL_TEXTURE_2D, ..)
	 * @param dataFormat (GL_RGB8, ..)
	 * @param textureFormat (GL_RGB, ..)
	 * @param filterMode (GL_LINEAR, GL_NEAREST etc..)
	 * @param wrapMode (GL_REPEAT etc..)
	 * @param buffer The image data (One or multiple images dependent
	 *  on whether this is a cube map)
	 */
	private Texture(String identifier, int width, int height, int mode, int dataFormat, int internalFormat,
			int textureFormat, int filterMode, int wrapMode, boolean useMipMap, Buffer[] data) {
		textureMap.put(identifier, this);
		this.identifier = identifier;
		this.mode = mode;
		this.dataFormat = dataFormat;
		this.textureFormat = textureFormat;
		this.filterMode = filterMode;
		this.wrapMode = wrapMode;
		this.mipMapOn = useMipMap;
		this.internalFormat = internalFormat;
		if (width != height) {
			throw new Error(this.getClass().getName() + ": Only square textures are supported.");
		}
		if (((height - 1) & height) != 0) {
			throw new Error(this.getClass().getName() + ":  Only power of two textures are supported.");
		}
		this.width = width;
		this.height = height;
		this.data = data;
	}

	/**
	 * Protected constructor for sub-classes
	 */
	protected Texture(String identifier) {
		this.identifier = identifier;
		textureMap.put(identifier, this);
	}
	
	/**
	 * Uploads the texture to the GPU and configures the texture filtering
	 * parameters. Data is allowed to be NULL (in this case, no image data
	 * will be uploaded and memory will be reserved instead)
	 */
	public void initializeTexture(GL gl) {
		if (!initialized) {
			// Generate the ID
			int[] tex = new int[1];
			gl.glGenTextures(1, tex, 0);
			textureID = tex[0];

			gl.glEnable(mode);
			initializing = true;
			bindTexture(gl, 0);
			initializing = false;

			gl.glTexParameterf(mode, GL.GL_TEXTURE_WRAP_S, wrapMode);
			gl.glTexParameterf(mode, GL.GL_TEXTURE_WRAP_T, wrapMode);

			if (mipMapOn) {
				gl.glTexParameterf(mode, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
				gl.glTexParameterf(mode, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
				gl.glTexParameterf(mode, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			} else {
				gl.glTexParameterf(mode, GL.GL_TEXTURE_MIN_FILTER, filterMode);
				gl.glTexParameterf(mode, GL.GL_TEXTURE_MAG_FILTER, filterMode);
			}

			if (mode == GL.GL_TEXTURE_CUBE_MAP) {
				/* Cube map - 6 bitmaps */
				for (int i=0; i<6; i++)
					gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, internalFormat, 
							width, height, 0, textureFormat, dataFormat, 
							data == null ? null : data[i]);
			} else {
				/* 1D or 2D texture - 1 bitmap */
				gl.glTexImage2D(mode, 0, internalFormat, width, height, 0, textureFormat, dataFormat, 
						data == null ? null : data[0]);
			}
			gl.glDisable(mode);
			data = null;
			initialized = true;
		}
	}

	/**
	 * Renders a screen filling quadrilateral with this texture applied.
	 */
	public void blit(GL gl) {
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		for (int i=0; i<4; i++) {
			gl.glActiveTexture(i+GL.GL_TEXTURE0);
			gl.glDisable(GL.GL_TEXTURE_2D);
		}

		/* Bind the texture to texture unit 0 */
		bindTexture(gl, 0);

		/* Draw a texture-mapped quad */
		gl.glColor4f(1, 1, 1, 1);

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(-1, -1, .5f);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(1, -1, .5f);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(1, 1, .5f);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(-1, 1, .5f);
		gl.glEnd();

		/* Deactivate texturing */
		unbindTexture(gl);
	}

	/**
	 * Bind this texture as the currently used texture. Also
	 * enables texturing with this specific type of texture (1d/2d/3d/cubemap).
	 */
	public void bindTexture(GL gl, int textureUnit) {
		if (!initialized && !initializing)
			initializeTexture(gl);
		gl.glActiveTexture(GL.GL_TEXTURE0 + textureUnit);
		gl.glEnable(mode);
		gl.glBindTexture(mode, textureID);
	}

	/**
	 * Disables texturing for this texture's texture type (1d/2d/3d/cubemap)
	 */
	public void unbindTexture(GL gl) {
		gl.glDisable(mode);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Loading and static constructors
	////////////////////////////////////////////////////////////////////////////////////////////////////	
	/** Maps texture instances to file names so files are not duplicated */
	protected static final HashMap<String, Texture> textureMap = new HashMap<String, Texture>();

	/**
	 * Returns a texture object for the given identifier. This method attempts
	 * to return a previously created texture, and throws a
	 * UndefinedTextureException if it fails
	 */
	public static final Texture getTexture(String identifier) throws UndefinedTextureException {
		Texture output = textureMap.get(identifier);
		if (output == null) {
			output = Texture.create2DTextureFromFile(identifier, identifier, GL.GL_LINEAR, GL.GL_REPEAT, true, false);
		}
		return output;
	}

	private static final Texture createTexture(String identifier, int width, int height, int mode, int dataFormat,
			int internalFormat, int textureFormat, int filterMode, int wrapMode, boolean useMipMap, Buffer[] data) {
		Texture toReturn = textureMap.get(identifier);
		if (toReturn != null)
			throw new Error("Requested new texture identifier already exists: " + identifier);
		toReturn = new Texture(identifier, width, height, mode, dataFormat, internalFormat,
				textureFormat, filterMode, wrapMode, useMipMap, data);
		return toReturn;
	}

	/**
	 * Creates a new texture and returns it
	 */
	public static final Texture createEmptyTexture(String identifier, int width, int height, int mode, 
			int dataFormat, int internalFormat, int textureFormat, int filterMode, int wrapMode, boolean useMipMap) {
		return createTexture(identifier, width, height, mode, dataFormat, internalFormat,
				textureFormat, filterMode, wrapMode, useMipMap, null);
	}

	/**
	 * Create a image data buffer from an image file
	 */
	public static Buffer createBufferFromImage(BufferedImage bufferedImage, boolean isLinear) {
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();

		ByteBuffer imageData = BufferUtil.newByteBuffer(imageWidth * imageHeight * 4);
		for (int iy = 0; iy < imageHeight; iy++) {
			for (int ix = 0; ix < imageWidth; ix++) {
				int pixelValue = bufferedImage.getRGB(ix, imageHeight - 1 - iy);
				float a = (((pixelValue & 0xff000000) >> 24) & 0xFF) / 255.0f;
				float r = ((pixelValue & 0x00ff0000) >> 16) / 255.0f;
				float g = ((pixelValue & 0x0000ff00) >> 8) / 255.0f;
				float b = ((pixelValue & 0x000000ff) >> 0) / 255.0f;
				if (isLinear) {
					/* Undo 1/2.2f gamma applied by ImageIO */
					r = (float) Math.pow(r, 2.2f);
					g = (float) Math.pow(g, 2.2f);
					b = (float) Math.pow(b, 2.2f);
				}

				imageData.put((iy * imageWidth + ix) * 4 + 0, (byte) (r*255));
				imageData.put((iy * imageWidth + ix) * 4 + 1, (byte) (g*255));
				imageData.put((iy * imageWidth + ix) * 4 + 2, (byte) (b*255));
				imageData.put((iy * imageWidth + ix) * 4 + 3, (byte) (a*255));
			}
		}
		return imageData;
	}
	
	/**
	 * Load an image file and return it as a BufferedImage
	 */
	public static BufferedImage loadImageFromFile(String imagePath) {
		BufferedImage bufferedImage;
		File imageFile = new File(imagePath);
		try {
			bufferedImage = ImageIO.read(imageFile);
		} catch (IOException e) {
			throw new Error("CS569.Texture.Texture.loadImageFromFile(): Error loading texture file: "
					+ imageFile.getAbsolutePath());
		}
		return bufferedImage;
	}

	/**
	 * Load the image into a file and transcribe into a byte array. This method
	 * only works for 1D and 2D images (no cube maps)
	 */
	public static final Texture create2DTextureFromFile(String identifier, String imageFileName, int filterMode, int wrapMode,
			boolean useMipMap, boolean isLinear) {
		//System.out.println("Loading texture \"" + imageFileName + "\" ..");
		URL resURL = Texture.class.getResource(imageFileName);
		BufferedImage bufferedImage = loadImageFromFile(resURL == null ? imageFileName : resURL.getFile());
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		Buffer imageData = createBufferFromImage(bufferedImage, isLinear);

		return createTexture(identifier, imageWidth, imageHeight, GL.GL_TEXTURE_2D, GL.GL_UNSIGNED_BYTE,
				GL.GL_RGBA8, GL.GL_RGBA, filterMode, wrapMode, useMipMap, new Buffer[] { imageData });
	}

	/**
	 * Load a set of images and transcribe into a byte array. This method only works for cubemaps.
	 * It requires a path prefix (i.e. "/home/user/myCubeMap_") and a postfix (i.e. ".png")
	 */
	public static final Texture createCubeMapFromFile(String identifier, String imageFilePrefix, 
			String imageFilePostfix) {
		int imageSize=0;
		String cubeMapPostfix[] = {
				"right", "left", "bottom", "top", "front", "back"
		};
		Buffer[] buffers = new Buffer[6];

		for (int i=0; i<6; i++) {
			BufferedImage image = loadImageFromFile(Texture.class.getResource(imageFilePrefix
					+ cubeMapPostfix[i] + imageFilePostfix).getFile());
			buffers[i] = createBufferFromImage(image, false);
			imageSize = image.getHeight();
		}

		return createTexture(identifier, imageSize, imageSize, GL.GL_TEXTURE_CUBE_MAP, GL.GL_UNSIGNED_BYTE,
				GL.GL_RGBA8, GL.GL_RGBA, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE, true, buffers);
	}
	
	/**
	 * Return the list of all previously created textures.
	 */
	public static final Iterator<String> getTextureList() {
		return getSortedTextureList().iterator();
	}
	
	/**
	 * Return the list of all previously created textures.
	 */
	public static final List<String> getSortedTextureList() {
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator<String> it = textureMap.keySet().iterator(); it.hasNext(); )
			result.add(it.next());
		Collections.sort(result);
		return result;
	}
	
	public String toString() {
		return new File(identifier).getName();
	}

	public int getMode() {
		return mode;
	}

	public int getTextureFormat() {
		return textureFormat;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
