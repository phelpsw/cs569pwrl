package cs569.panel;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import cs569.apps.Viewer;
import cs569.material.Material;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */

public abstract class MaterialPanel extends JPanel implements ActionListener,
		ChangeListener {

	public static final String MATERIAL_PANEL_UPDATE = "MAT UPDATE";

	protected Material baseMat;

	protected MaterialPanel() {
		addPropertyChangeListener(MATERIAL_PANEL_UPDATE, Viewer.getMainViewer());
		setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		setLayout(null);
	}

	/**
	 * Fire an update to the viewer for this material.
	 */
	protected void fireUpdate() {
		firePropertyChange(MATERIAL_PANEL_UPDATE, true, false);
	}

	protected int extractInt(Object source, int min, int max) {
		int value;
		JInputField tf = (JInputField) source;
		try {
			value = Integer.parseInt(tf.getText());
			if (value < min) {
				value = min;
			} else if (value > max) {
				value = max;
			}
		} catch (NumberFormatException exp) {
			value = min;
		}

		tf.setText("" + value);
		return value;
	}

	protected float extractFloat(JInputField tf, float min, float max) {
		float value;
		try {
			value = Float.parseFloat(tf.getText());
			if (value < min) {
				value = min;
			} else if (value > max) {
				value = max;
			}
		} catch (NumberFormatException exp) {
			value = min;
		}

		tf.setText("" + value);
		return value;
	}
}