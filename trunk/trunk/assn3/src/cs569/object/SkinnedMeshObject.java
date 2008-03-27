package cs569.object;

import java.io.PrintStream;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;

import com.sun.opengl.util.BufferUtil;

import cs569.misc.WritingUtils;

/**
 * Extended mesh object with support for per-vertex weights&indices.
 * Takes care of passing this information to a vertex shader
 * which performs the actual skinning operations.
 * 
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class SkinnedMeshObject extends TangentSpaceMeshObject {
	/** Multiple sets of skinning weights (This array has 'attributeCount' entries) */
	protected FloatBuffer[] weights;

	/** Associated bone indices (floating point values because GLSL wants
	    them that way. They need to be re-cast into integers within the
	    shader) */
	protected FloatBuffer[] weightIndices;

	/** Transformations for conversion between bone space and object space */
	protected Matrix4f[] offsetTransforms;

	/** Bone names */
	protected String[] boneNames;
	
	/** The number of bones that are used to skin this mesh */
	protected int boneCount;

	/** The number of additional attributes per vertex */
	protected int attributeCount;
	
	/**
	 * Should be used only be the Parser. Please name your objects!
	 */
	public SkinnedMeshObject() {
		/* The skinned object is transformed by its skeleton
		 * and should not be moved by itself.
		 */
		setUpdateList(NO_UPDATE);
	}

	/**
	 * Create an empty skinned mesh object
	 */
	public SkinnedMeshObject(String inName) {
		super(inName);
		/* The skinned object is transformed by its skeleton
		 * and should not be moved by itself.
		 */
		setUpdateList(NO_UPDATE);
	}

	/**
	 * Verify that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly
	 */
	@Override
	protected boolean isConfiguredForShader(GL gl) {
		if (!super.isConfiguredForShader(gl))
			return false;

		// TODO
		return true;
	}

	/**
	 * Ensure that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly
	 */
	@Override
	protected void configureForShader(GL gl) {
		super.configureForShader(gl);
		
		/// TODO
	}

	/**
	 * Pass on skinning data to the shader
	 */
	@Override
	protected void connectToShader(GL gl) {
		super.connectToShader(gl);
		
		/// TODO
	}

	/**
	 * Disconnect the mesh-related shader uniforms/attributes
	 */
	@Override
	protected void disconnectFromShader(GL gl) {
		super.disconnectFromShader(gl);
		
		/// TODO
	}
	
	/**
	 * Should only be used by the Parser.
	 * 
	 * @param weights
	 */
	public void setWeights(double[] weights) {
		if (weights.length % numVertices != 0) {
			throw new Error(
					"CS569.Skin.SkinnedMeshObject.setWeights(): Weight array length is not a multiple of the vertex count. ("+weights.length+" vs " + numVertices + ")");
		}
		int setCount = weights.length / numVertices;
		this.weights = new FloatBuffer[setCount];
		for (int i=0; i<setCount; i++) {
			this.weights[i] = BufferUtil.newFloatBuffer(numVertices);
			for (int j=0; j<numVertices; j++) {
				this.weights[i].put((float) weights[j+i*numVertices]);
			}
		}
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param weightIndices
	 */
	public void setWeightIndices(int[] indices) {
		if (indices.length % numVertices != 0) {
			throw new Error(
					"CS569.Skin.SkinnedMeshObject.setWeightIndices(): Weight index array length is not a multiple of the vertex count.");
		}
		int setCount = indices.length / numVertices;
		this.weightIndices = new FloatBuffer[setCount];
		for (int i=0; i<setCount; i++) {
			this.weightIndices[i] = BufferUtil.newFloatBuffer(numVertices);
			for (int j=0; j<numVertices; j++) {
				this.weightIndices[i].put(indices[j+i*numVertices]);
			}
		}
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setOffsetTransformArray(double[] data) {
		if (data.length % 16 != 0) {
			throw new Error(
					"CS569.Skin.SkinnedMeshObject.setOffsetTransformArray(): Value array length is not a multiple of 16.");
		}
		int boneCount = data.length / 16;
		offsetTransforms = new Matrix4f[boneCount];
		for (int i=0; i<boneCount; i++) {
			offsetTransforms[i] = new Matrix4f();
			float[] temp = new float[16];
			for (int j = 0; j < 16; j++)
				temp[j] = (float) data[j+i*16];
			offsetTransforms[i].set(temp);
			offsetTransforms[i].transpose();
		}
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setAttributeCount(int attributeCount) {
		this.attributeCount = attributeCount;
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setBoneCount(int boneCount) {
		this.boneCount = boneCount;
	}
	
	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setBoneNames(String[] boneNames) {
		this.boneNames = boneNames;
	}

	/**
	 * @see cs569.object.HierarchicalObject#writeLocalData(java.io.PrintStream,
	 *      int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		super.writeLocalData(out, indent);
		WritingUtils.writeInt(out, boneCount, "boneCount", indent);
		WritingUtils.writeInt(out, attributeCount, "attributeCount", indent);
		WritingUtils.writeStringArray(out, boneNames, "boneNames", indent);
		out.print(WritingUtils.getIndentString(indent)+"<weights>");
		for (int i=0; i<this.weights.length; i++) {
			FloatBuffer weights = (FloatBuffer) this.weights[i];
			weights.rewind();
			for (int j = 0; j < weights.capacity(); j++) {
				out.print(weights.get() + " ");
			}
		}
		out.println("</weights>");
		out.print(WritingUtils.getIndentString(indent)+"<weightIndices>");
		for (int i=0; i<this.weightIndices.length; i++) {
			FloatBuffer indices = (FloatBuffer) this.weightIndices[i];
			indices.rewind();
			for (int j = 0; j < indices.capacity(); j++) {
				out.print((int) indices.get() + " ");
			}
		}
		out.println("</weightIndices>");
		out.print(WritingUtils.getIndentString(indent)+"<offsetTransformArray>");
		for (int i=0; i<boneCount; i++) {
			Matrix4f trafo = new Matrix4f(offsetTransforms[i]);
			trafo.transpose();
			out.print(trafo.toString().replace('\n', ' ').replace(',', ' ')+" ");			
		}
		out.println("</offsetTransformArray>");
	}
}