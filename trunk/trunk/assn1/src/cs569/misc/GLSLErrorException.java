package cs569.misc;

import javax.media.opengl.GL;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob (GLSL port)
 */
public class GLSLErrorException extends Exception {
	/**
	 * Creates an exception
	 * 
	 * @param s
	 */
	public GLSLErrorException(String s) {
		super(s);
	}

	/**
	 * Throws an exception if there is a GLSL Error
	 */
	public static void checkError(GL gl, int id) throws GLSLErrorException {
		int[] status = new int[1];
		if (gl.glIsShader(id))
			gl.glGetShaderiv(id, GL.GL_COMPILE_STATUS, status, 0);
		else
			gl.glGetProgramiv(id, GL.GL_LINK_STATUS, status, 0);
		String infoLog = getInfoLog(gl, id);
		if (status[0] == GL.GL_FALSE)
			throw new GLSLErrorException(getInfoLog(gl, id));
		else if (!infoLog.trim().equals(""))
			System.out.println(infoLog); /* May contain helpful warnings */
	}

	public static String getInfoLog(GL gl, int id) throws GLSLErrorException {
		int maxLength[] = new int[1];
		if (gl.glIsShader(id))
			gl.glGetShaderiv(id, GL.GL_INFO_LOG_LENGTH, maxLength, 0);
		else
			gl.glGetProgramiv(id, GL.GL_INFO_LOG_LENGTH, maxLength, 0);

		byte infoLog[] = new byte[maxLength[0]];
		int length[] = new int[1];

		if (maxLength[0] > 0) {
			if (gl.glIsShader(id))
				gl.glGetShaderInfoLog(id, maxLength[0], length, 0, infoLog, 0);
			else
				gl.glGetProgramInfoLog(id, maxLength[0], length, 0, infoLog, 0);
		}

		return new String(infoLog);
	}
}