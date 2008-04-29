package cs569.animation;

import javax.vecmath.Vector3f;

/**
 * Created on March 5, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */

public class TranslationTrack extends AnimationTrack<Vector3f> {
	private Vector3f interpVec = new Vector3f();

	public TranslationTrack() {
	}

	public Keyframe createKeyframe(float[] data) {
		Keyframe keyframe = new Keyframe();
		assert(data.length == 4);
		keyframe.time = data[0];
		keyframe.value = new Vector3f(data[1], data[2], data[3]);
		return keyframe;
	}

	public void update(float relPos, Vector3f kfA, Vector3f kfB) {
		interpVec.set(kfA);
		interpVec.scale(relPos);
		interpVec.scaleAdd(1.0f-relPos, kfB, interpVec);
		object.setTranslate(interpVec);
	}
}
