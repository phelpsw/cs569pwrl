package cs569.panel;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;

import cs569.apps.Viewer;
import cs569.material.Phong;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class PhongPanel extends MaterialPanel {
	Phong phong;

	public PhongPanel(Phong material) {
		baseMat = material;
		phong = material;
		createContents();
	}

	public void createContents() {
		JLabel label;
		JInputField text;
		String strTemp;

		label = new JLabel("Diffuse: ");
		label.setBounds(15, 15, 60, 25);
		add(label);

		strTemp = "" + phong.getDiffuseColor().x;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 15, 45, 25);
		text.setActionCommand("diffx");
		text.addActionListener(this);
		add(text);

		strTemp = "" + phong.getDiffuseColor().y;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(135, 15, 45, 25);
		text.setActionCommand("diffy");
		text.addActionListener(this);
		add(text);

		strTemp = "" + phong.getDiffuseColor().z;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(185, 15, 45, 25);
		text.setActionCommand("diffz");
		text.addActionListener(this);
		add(text);

		// --------

		label = new JLabel("Specular: ");
		label.setBounds(15, 45, 60, 25);
		add(label);

		strTemp = "" + phong.getSpecularColor().x;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 45, 45, 25);
		text.setActionCommand("specx");
		text.addActionListener(this);
		add(text);

		strTemp = "" + phong.getSpecularColor().y;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(135, 45, 45, 25);
		text.setActionCommand("specy");
		text.addActionListener(this);
		add(text);

		strTemp = "" + phong.getSpecularColor().z;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(185, 45, 45, 25);
		text.setActionCommand("specz");
		text.addActionListener(this);
		add(text);

		// --------

		label = new JLabel("Exponent: ");
		label.setBounds(15, 75, 60, 25);
		add(label);

		strTemp = "" + phong.getExponent();
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 75, 45, 25);
		text.setActionCommand("exp");
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
				phong.getDiffuseColor().x = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("diffy")) {
				phong.getDiffuseColor().y = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("diffz")) {
				phong.getDiffuseColor().z = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("specx")) {
				phong.getSpecularColor().x = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("specy")) {
				phong.getSpecularColor().y = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("specz")) {
				phong.getSpecularColor().z = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("exp")) {
				phong.setExponent(extractFloat(tf, 0.0f, Float.MAX_VALUE));
			}
		}

		fireUpdate();
		Viewer.requestRepaint();
	}
}