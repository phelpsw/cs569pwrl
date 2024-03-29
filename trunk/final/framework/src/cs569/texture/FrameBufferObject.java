package cs569.texture;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cs569.misc.GLSLErrorException;
import cs569.object.HierarchicalObject;

/**
 * Encapsulates an OpenGL frame buffer object
 * 
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public abstract class FrameBufferObject extends Texture {
	/** Identifies a frame buffer object, which renders to a color texture */
	public final static int COLOR_TEXTURE_FBO = 0;

	/** Identifies a frame buffer object, which only renders to a depth texture (no color target) */
	public final static int DEPTH_TEXTURE_FBO = 1;

	/** Identifies a frame buffer object, which renders to sides of a cube map */
	public final static int CUBEMAP_TEXTURE_FBO = 2;
	
	/** Identifies a frame buffer object that acts as an HDR render target */
	public final static int HDR_TEXTURE_FBO = 3;

	/** OpenGL handle for this FBO */
	protected int fboId;

	/** FBO type identifier (one of DEPTH_TEXTURE_FBO and CUBEMAP_TEXTURE_FBO)*/
	protected int fboType;

	/** Has the frame buffer texture been written to? */
	protected boolean dirty;

	/**
	 * Internal constructor. Gets called by super-classes
	 * "fboType" should be one of COLOR_TEXTURE_FBO, DEPTH_TEXTURE_FBO or CUBEMAP_TEXTURE_FBO.
	 * "resolution" should be a power of two and denotes the side length of the target texture.
	 */
	protected FrameBufferObject(String identifier, int fboType, int width, int height) {
		super(identifier);

		this.fboType = fboType;
		this.width = width;
		this.height = height;
		this.dirty = false;

		/* Configure the underlying texture's parameters */
		switch (fboType) {
			case COLOR_TEXTURE_FBO:
				mode = GL.GL_TEXTURE_2D;
				textureFormat = GL.GL_RGB;
				internalFormat = GL.GL_RGB8;
				dataFormat = GL.GL_UNSIGNED_BYTE;
				mipMapOn = true;
				filterMode = GL.GL_LINEAR_MIPMAP_LINEAR;
				break;
			case CUBEMAP_TEXTURE_FBO:
				mode = GL.GL_TEXTURE_CUBE_MAP;
				textureFormat = GL.GL_RGBA;
				internalFormat = GL.GL_RGBA;
				dataFormat = GL.GL_UNSIGNED_BYTE;
				filterMode = GL.GL_LINEAR;
				mipMapOn = true;
				break;
			case DEPTH_TEXTURE_FBO:
				mode = GL.GL_TEXTURE_2D;
				textureFormat = GL.GL_DEPTH_COMPONENT;
				internalFormat = GL.GL_DEPTH_COMPONENT32;
				dataFormat = GL.GL_UNSIGNED_BYTE;
				mipMapOn = false;
				filterMode = GL.GL_LINEAR;
				break;
			case HDR_TEXTURE_FBO:
				mode = GL.GL_TEXTURE_2D;
				textureFormat = GL.GL_RGBA;
				internalFormat = GL.GL_RGBA32F_ARB;
				dataFormat = GL.GL_FLOAT;
				mipMapOn = false;
				filterMode = GL.GL_NEAREST;
				break;
			default:
				throw new Error("Unknown FBO type requested!");
		}

		/* Just allocate texture memory, their contents are dynamic. */
		data = null;
		
		wrapMode = GL.GL_CLAMP;
	}

	/**
	 * Initialize the frame buffer object plus associated textures and renderbuffers.
	 * Called by cs569.apps.Viewer.init()
	 */
	public void initializeFBO(GL gl) {
		int id[] = new int[1];
		gl.glGenFramebuffersEXT(1, id, 0);
		fboId = id[0];

		bindFBO(gl);
		initializeTexture(gl);

		if (fboType == COLOR_TEXTURE_FBO || fboType == CUBEMAP_TEXTURE_FBO || fboType == HDR_TEXTURE_FBO) {	
			int depthId[] = new int[1];

			/* A depth renderbuffer is needed for these FBO types */
			gl.glGenRenderbuffersEXT(1, depthId, 0);
			gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthId[0]);
			gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT, 
					width, height);

			/* Attach it to the FBO */
			gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT, 
					GL.GL_RENDERBUFFER_EXT, depthId[0]);
		} 

		if (mipMapOn)
            gl.glGenerateMipmapEXT(mode);

		if (fboType == COLOR_TEXTURE_FBO) {
			/* Attach the texture as color target */
			gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,
	                   mode, textureID, 0);
		} else if (fboType == CUBEMAP_TEXTURE_FBO) {
			/* Attach the texture as color target - doesn't really matter which cube map position */
			gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,
	                   GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X, textureID, 0);
		} else if (fboType == DEPTH_TEXTURE_FBO) {
			/* Attach the texture as depth target */
			gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT,
	                   mode, textureID, 0);

			/* Configure the depth texture for depth comparison */
            gl.glTexParameteri(mode, GL.GL_TEXTURE_COMPARE_MODE, GL.GL_COMPARE_R_TO_TEXTURE);
			gl.glTexParameteri(mode, GL.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);

			/* This FBO won't store/draw/read color values */
			gl.glDrawBuffer(GL.GL_FALSE);
			gl.glReadBuffer(GL.GL_FALSE);
		} else if (fboType == HDR_TEXTURE_FBO) {
			gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT,
					mode, textureID, 0);
		}

		checkForErrors(gl);
		unbindFBO(gl);
	}

	/**
	 * Check for any issues that OpenGL might have with the
	 * current FBO configuration (to be used right after creation of
	 * the FBO!) This should help when things don't work as expected.
	 */
	protected void checkForErrors(GL gl) {
		int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		switch (status) {
		case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
			break;
			/* GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENTS_EXT (constant
			   not available in all versions of JOGL */
		case 0x8CD6: 
			throw new FrameBufferObjectException("Incomplete attachment");
		case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
			throw new FrameBufferObjectException("Unsupported framebuffer format");
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
			throw new FrameBufferObjectException("Incomplete framebuffer - duplicate attachment");
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			throw new FrameBufferObjectException("Incomplete framebuffer - missing attachment");
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			throw new FrameBufferObjectException("Incomplete framebuffer - invalid dimensions");
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
			throw new FrameBufferObjectException("Incomplete framebuffer - no draw buffer");
		case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
			throw new FrameBufferObjectException("Incomplete framebuffer - no readbuffer");
		default:
			throw new FrameBufferObjectException("Unknown error status");
		}
	}

	/**
	 * Activates this FBO as target for upcoming OpenGL drawing commands.
	 */
	protected void bindFBO(GL gl) {
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, fboId);
		gl.glPushAttrib(GL.GL_VIEWPORT);
		gl.glViewport(0, 0, width, height);
	}

	/**
	 * Switches back to the default framebuffer
	 */
	protected void unbindFBO(GL gl) {
		gl.glPopAttrib();
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
		dirty = true;
	}

	/**
	 * Activate the FBO as the current render target, clears
	 * the depth/color buffers and hands control to the sub-class
	 */
	public void render(GL gl, GLU glu, HierarchicalObject object) throws GLSLErrorException {
		bindFBO(gl);
		if (fboType == COLOR_TEXTURE_FBO || fboType == CUBEMAP_TEXTURE_FBO || fboType == HDR_TEXTURE_FBO)
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		else
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		/* Let the subclass implement the actual rendering */
		renderImpl(gl, glu, object);

		unbindFBO(gl);
	}
	
	/**
	 * To be implemented in sub-classes
	 */
	public abstract void renderImpl(GL gl, GLU glu, HierarchicalObject object)
		throws GLSLErrorException;
	
	/**
	 * Activates this FBO's texture target as a normal texture
	 * for drawing. Should not be used while simultaneously
	 * rendering to that texture!
	 */
	@Override
	public void bindTexture(GL gl, int textureUnit) {
		super.bindTexture(gl, textureUnit);
		/* Re-generate MIP-maps if necessary */
		if (mipMapOn && dirty) {
			gl.glGenerateMipmapEXT(mode);
			dirty = false;
		}
	}
}
