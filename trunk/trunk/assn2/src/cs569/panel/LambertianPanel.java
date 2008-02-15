package cs569.panel;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;

import cs569.apps.Viewer;
import cs569.material.Lambertian;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */

public class LambertianPanel extends MaterialPanel {

	Lambertian lambertian;

	public LambertianPanel(Lambertian material) {
		baseMat = material;
		lambertian = material;
		createContents();
	}

	public void createContents() {
		JLabel label;
		JInputField text;
		String strTemp;

		label = new JLabel("Diffuse: ");
		label.setBounds(15, 15, 60, 25);
		add(label);

		strTemp = "" + lambertian.getDiffuseColor().x;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 15, 45, 25);
		text.setActionCommand("diffx");
		text.addActionListener(this);
		add(text);

		strTemp = "" + lambertian.getDiffuseColor().y;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(135, 15, 45, 25);
		text.setActionCommand("diffy");
		text.addActionListener(this);
		add(text);

		strTemp = "" + lambertian.getDiffuseColor().z;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(185, 15, 45, 25);
		text.setActionCommand("diffz");
		text.addActionListener(this);
		add(text);
	}

	public void stateChanged(ChangeEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		String argument = e.getActionCommand();

		if (e.getSource() instanceof JInputField) {
			JInputField tf = (JInputField) e.getSource();

			if (argument.equals("diffx")) {
				lambertian.getDiffuseColor().x = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("diffy")) {
				lambertian.getDiffuseColor().y = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("diffz")) {
				lambertian.getDiffuseColor().z = extractFloat(tf, 0.0f, 1.0f);
			}
		}

		fireUpdate();
		Viewer.requestRepaint();
	}
}