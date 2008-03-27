package cs569.misc;

import javax.vecmath.Matrix4f;

/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class GLUtils {
	/**
	 * Convert a Matrix4f matrix into an OpenGL column-major
	 * float array
	 */
	public static float[] fromMatrix4f(Matrix4f m) {
		float[] result = {
			m.m00, m.m10, m.m20, m.m30,
			m.m01, m.m11, m.m21, m.m31,
			m.m02, m.m12, m.m22, m.m32,
			m.m03, m.m13, m.m23, m.m33
		};
		
		return result;
	}

	/**
	 * Convert an OpenGL column-major float array
	 * into a Matrix4f matrix
	 */
	public static Matrix4f fromFloatArray(float[] data) {
		return new Matrix4f(
			data[0], data[4], data[8], data[12],
			data[1], data[5], data[9], data[13],
			data[2], data[6], data[10], data[14],
			data[3], data[7], data[11], data[15]
		);
	}
}
