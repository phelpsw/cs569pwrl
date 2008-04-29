package cs569.object;

import java.io.PrintStream;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;

import com.sun.opengl.util.BufferUtil;

import cs569.misc.GLUtils;
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
	/** Multiple per-vertex sets of skinning weights */
	protected FloatBuffer[] weights;

	/** Associated bone indices (floating point values because GLSL wants
	    them that way. They need to be re-cast into integers within the
	    shader) */
	protected FloatBuffer[] weightIndices;

	/** Transformations for conversion between bone space and object space */
	protected Matrix4f[] offsetTransforms;

	/** Final transformation matrices (bone world transform + offset transform) */
	protected Matrix4f[] finalTransforms;

	/** Bone names */
	protected String[] boneNames;
	
	/** Bone references */
	protected Group[] bones;

	/** The number of bones that are used to skin this mesh */
	protected int boneCount;

	/** The number of additional attributes per vertex */
	protected int attributeCount;

	/** GLSL handle used to communicate bone transformations (Uniform) */
	protected int[] boneTransformHandle;

	/** GLSL handle used to communicate weights (Attribute) */
	protected int[] weightsHandle;

	/** GLSL handle used to communicate weight indices (Attribute) */
	protected int[] weightIndicesHandle;
	
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

		if (boneTransformHandle == null || weightsHandle == null 
				|| weightIndicesHandle == null)
			return false;
		for (int i=0; i<boneCount; i++) {
			if (boneTransformHandle[i]==-1)
					return false;
		}
		for (int i=0; i<attributeCount; i++) {
			if (weightsHandle[i]==-1 || weightIndicesHandle[i] == -1)
					return false;
		}
		return true;
	}

	/**
	 * Ensure that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly
	 */
	@Override
	protected void configureForShader(GL gl) {
		super.configureForShader(gl);
		/* What is the currently running program? */
		
		if (program != 0) {
			boneTransformHandle = new int[boneCount];
			weightsHandle = new int[attributeCount];
			weightIndicesHandle = new int[attributeCount];
			for (int bone=0; bone<boneCount; bone++) {
				boneTransformHandle[bone] = gl.glGetUniformLocation(
						program, "boneTransform["+ bone +"]");
			}
			for (int attr=0; attr<attributeCount; attr++) {
				weightsHandle[attr] = gl.glGetAttribLocation(
						program, "weight"+attr);
				weightIndicesHandle[attr] = gl.glGetAttribLocation(
						program, "weightIndex"+attr);
			}
			
			/* Lookup all referenced bones in the scene hierarchy */
			HierarchicalObject obj = this;
			while (obj.getParent() != null)
				obj = (HierarchicalObject) obj.getParent();
			bones = new Group[boneCount];
			finalTransforms = new Matrix4f[boneCount];
			for (int i=0; i<boneNames.length; i++) {
				bones[i] = (Group) obj.findByName(boneNames[i]);
				finalTransforms[i] = new Matrix4f();
				if (bones[i] == null)
					throw new Error("CS569.Skin.SkinnedMeshObject.configureForShader(): Bone '" + boneNames[i] + "' not found!");
			}
		}
	}

	/**
	 * Pass on skinning data to the shader
	 */
	@Override
	protected void connectToShader(GL gl) {
		super.connectToShader(gl);
		for (int bone=0; bone<boneCount; bone++) {
			finalTransforms[bone].mul(bones[bone].getWorldTransform(),
					offsetTransforms[bone]);
			float[] data = GLUtils.fromMatrix4f(finalTransforms[bone]);
			gl.glUniformMatrix4fv(boneTransformHandle[bone], 1, false, data, 0);
		}
		for (int attr=0; attr<attributeCount; attr++) {
			weights[attr].rewind();
			weightIndices[attr].rewind();
			gl.glEnableVertexAttribArray(weightsHandle[attr]);
			gl.glEnableVertexAttribArray(weightIndicesHandle[attr]);
			gl.glVertexAttribPointer(weightsHandle[attr], 1, GL.GL_FLOAT, false, 0, weights[attr]);
			gl.glVertexAttribPointer(weightIndicesHandle[attr], 1, GL.GL_FLOAT, false, 0, weightIndices[attr]);
		}
	}

	/**
	 * Disconnect the mesh-related shader uniforms/attributes
	 */
	@Override
	protected void disconnectFromShader(GL gl) {
		super.disconnectFromShader(gl);
		for (int attr=0; attr<attributeCount; attr++) {
			gl.glDisableVertexAttribArray(weightsHandle[attr]);
			gl.glDisableVertexAttribArray(weightIndicesHandle[attr]);
		}
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