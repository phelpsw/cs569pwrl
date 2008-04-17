package cs569.misc;

import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class WritingUtils {

	/** The size of an indention step. */
	public static final int INDENTION_STEP = 2;

	/**
	 * Returns a string of indent spaces. Used to indent lines.
	 * 
	 * @param indent
	 * @return
	 */
	public static final String getIndentString(int indent) {
		String out = "";
		while (out.length() < indent) {
			out += " ";
		}
		return out;
	}

	/**
	 * Returns the open tag for the given name
	 * 
	 * @param tagName
	 * @return
	 */
	public static final String getTagOpen(String tagName, int indent) {
		return getIndentString(indent) + "<" + tagName + ">";
	}

	/**
	 * Returns the close tag for a given name
	 * 
	 * @param tagName
	 * @return
	 */
	public static final String getTagClose(String tagName, int indent) {
		return getIndentString(indent) + "</" + tagName + ">";
	}

	/**
	 * Writes the tag for a color.
	 * 
	 * @param color
	 * @param tagname
	 * @param indent
	 */
	public static final void writeColor(PrintStream out, Color3f color,
			String tagName, int indent) {
		out.println(getTagOpen(tagName, indent) + color.x + " " + color.y + " "
				+ color.z + getTagClose(tagName, 0));
	}

	/**
	 * Writes the tag for a double.
	 * 
	 * @param out
	 * @param value
	 * @param tagName
	 * @param indent
	 */
	public static final void writeDouble(PrintStream out, double value,
			String tagName, int indent) {
		out.println(getTagOpen(tagName, indent) + value
				+ getTagClose(tagName, 0));
	}

	/**
	 * Writes the tag for a double.
	 * 
	 * @param out
	 * @param value
	 * @param tagName
	 * @param indent
	 */
	public static final void writeInt(PrintStream out, int value,
			String tagName, int indent) {
		out.println(getTagOpen(tagName, indent) + value
				+ getTagClose(tagName, 0));
	}

	/**
	 * Writes the tag for a Tuple3f
	 * 
	 * @param out
	 * @param tuple
	 * @param tagName
	 * @param indent
	 */
	public static final void writeTuple3f(PrintStream out, Tuple3f tuple,
			String tagName, int indent) {
		out.println(getTagOpen(tagName, indent) + tuple.x + " " + tuple.y + " "
				+ tuple.z + getTagClose(tagName, 0));
	}

	/**
	 * Writes the tag for a Tuple4f
	 * 
	 * @param out
	 * @param tuple
	 * @param tagName
	 * @param indent
	 */
	public static final void writeTuple4f(PrintStream out, Tuple4f tuple,
			String tagName, int indent) {
		out.println(getTagOpen(tagName, indent) + tuple.x + " " + tuple.y + " "
				+ tuple.z + " " + tuple.w + getTagClose(tagName, 0));
	}

	/**
	 * Writes an array of floats.
	 * 
	 * @param out
	 * @param data
	 * @param tagName
	 * @param indent
	 */
	public static final void writeFloatArray(PrintStream out, float[] data,
			String tagName, int indent) {
		out.print(getTagOpen(tagName, indent));
		for (int i = 0; i < data.length - 1; i++) {
			out.print(data[i] + " ");
		}
		out.println(data[data.length - 1] + getTagClose(tagName, 0));
	}

	/**
	 * Writes an array of strings.
	 * 
	 * @param out
	 * @param data
	 * @param tagName
	 * @param indent
	 */
	public static final void writeStringArray(PrintStream out, String[] data,
			String tagName, int indent) {
		out.print(getTagOpen(tagName, indent));
		for (int i = 0; i < data.length - 1; i++) {
			out.print(data[i] + " ");
		}
		out.println(data[data.length - 1] + getTagClose(tagName, 0));
	}

	/**
	 * Writes the tag for a FloatBuffer.
	 * 
	 * @param out
	 * @param data
	 * @param tagName
	 * @param indent
	 */
	public static final void writeFloatBuffer(PrintStream out,
			FloatBuffer data, String tagName, int indent) {
		out.print(getTagOpen(tagName, indent));
		data.rewind();
		for (int i = 0; i < data.capacity() - 1; i++) {
			out.print(data.get() + " ");
		}
		out.println(data.get() + getTagClose(tagName, 0));
	}

	/**
	 * Writes an array of ints.
	 * 
	 * @param out
	 * @param data
	 * @param tagName
	 * @param indent
	 */
	public static final void writeIntBuffer(PrintStream out, IntBuffer data,
			String tagName, int indent) {
		out.print(getTagOpen(tagName, indent));
		data.rewind();
		for (int i = 0; i < data.capacity() - 1; i++) {
			out.print(data.get() + " ");
		}
		out.println(data.get() + getTagClose(tagName, 0));
	}

	/**
	 * Writes a tag for a string
	 * 
	 * @param out
	 * @param str
	 * @param tagName
	 * @param indent
	 */
	public static final void writeString(PrintStream out, String str,
			String tagName, int indent) {
		out
				.println(getTagOpen(tagName, indent) + str
						+ getTagClose(tagName, 0));
	}
}
