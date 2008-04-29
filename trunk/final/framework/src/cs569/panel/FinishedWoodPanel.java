package cs569.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;

import cs569.apps.TronRuntime;
import cs569.material.FinishedWood;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */

public class FinishedWoodPanel extends MaterialPanel implements ItemListener {
	private FinishedWood finishedWood;
	private JComboBox diffuseMapComboBox, axisMapComboBox;
	private JComboBox fiberMapComboBox, betaMapComboBox;

	public FinishedWoodPanel(FinishedWood material) {
		baseMat = material;
		finishedWood = material;
		createContents();
	}

	public void createContents() {
		JLabel label;
		JInputField text;
		String strTemp;

		label = new JLabel("Specular: ");
		label.setBounds(15, 15, 60, 25);
		add(label);

		strTemp = "" + finishedWood.getSpecularColor().x;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 15, 45, 25);
		text.setActionCommand("specx");
		text.addActionListener(this);
		add(text);

		strTemp = "" + finishedWood.getSpecularColor().y;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(135, 15, 45, 25);
		text.setActionCommand("specy");
		text.addActionListener(this);
		add(text);

		strTemp = "" + finishedWood.getSpecularColor().z;
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(185, 15, 45, 25);
		text.setActionCommand("specz");
		text.addActionListener(this);
		add(text);

		// --------

		label = new JLabel("eta: ");
		label.setBounds(15, 45, 60, 25);
		add(label);

		strTemp = "" + finishedWood.getEta();
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 45, 45, 25);
		text.setActionCommand("eta");
		text.addActionListener(this);
		add(text);

		label = new JLabel("Rough.: ");
		label.setBounds(15, 75, 60, 25);
		add(label);

		strTemp = "" + finishedWood.getRoughness();
		strTemp = strTemp.length() > 5 ? strTemp.substring(0, 5) : strTemp;
		text = new JInputField(strTemp);
		text.setBounds(85, 75, 45, 25);
		text.setActionCommand("roughness");
		text.addActionListener(this);
		add(text);

		axisMapComboBox = new JComboBox();
		diffuseMapComboBox = new JComboBox();
		fiberMapComboBox = new JComboBox();
		betaMapComboBox = new JComboBox();

		for (Iterator<String> it = Texture.getTextureList(); it.hasNext(); ) {
			Texture texture = Texture.getTexture(it.next());
			if (texture.getMode() == GL.GL_TEXTURE_2D) {
				axisMapComboBox.addItem(texture);
				diffuseMapComboBox.addItem(texture);
				fiberMapComboBox.addItem(texture);
				betaMapComboBox.addItem(texture);
			}
		}
		diffuseMapComboBox.setSelectedItem(finishedWood.getDiffuseTexture());
		axisMapComboBox.setSelectedItem(finishedWood.getAxisTexture());
		fiberMapComboBox.setSelectedItem(finishedWood.getFiberTexture());
		betaMapComboBox.setSelectedItem(finishedWood.getBetaTexture());

		label = new JLabel("Diffuse :");
		label.setBounds(15, 105, 60, 25);
		add(label);
		diffuseMapComboBox.setBounds(85, 105, 150, 25);

		label = new JLabel("Axis :");
		label.setBounds(15, 135, 60, 25);
		add(label);
		axisMapComboBox.setBounds(85, 135, 150, 25);

		label = new JLabel("Fiber: ");
		label.setBounds(15, 165, 60, 25);
		add(label);
		fiberMapComboBox.setBounds(85, 165, 150, 25);

		label = new JLabel("Beta: ");
		label.setBounds(15, 195, 60, 25);
		add(label);
		betaMapComboBox.setBounds(85, 195, 150, 25);

		diffuseMapComboBox.addItemListener(this);
		axisMapComboBox.addItemListener(this);
		fiberMapComboBox.addItemListener(this);
		betaMapComboBox.addItemListener(this);
		add(diffuseMapComboBox);
		add(axisMapComboBox);
		add(fiberMapComboBox);
		add(betaMapComboBox);
	}
	
	public void itemStateChanged(ItemEvent e) {
		finishedWood.setDiffuseTexture((Texture) diffuseMapComboBox.getSelectedItem());
		finishedWood.setAxisTexture((Texture) axisMapComboBox.getSelectedItem());
		finishedWood.setFiberTexture((Texture) fiberMapComboBox.getSelectedItem());
		finishedWood.setBetaTexture((Texture) betaMapComboBox.getSelectedItem());
		fireUpdate();
		TronRuntime.requestRepaint();
	}

	public void stateChanged(ChangeEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		String argument = e.getActionCommand();

		if (e.getSource() instanceof JInputField) {
			JInputField tf = (JInputField) e.getSource();

			if (argument.equals("specx")) {
				finishedWood.getSpecularColor().x = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("specy")) {
				finishedWood.getSpecularColor().y = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("specz")) {
				finishedWood.getSpecularColor().z = extractFloat(tf, 0.0f, 1.0f);
			} else if (argument.equals("eta")) {
				finishedWood.setEta(extractFloat(tf, -Float.MAX_VALUE,
						Float.MAX_VALUE));
			} else if (argument.equals("roughness")) {
				finishedWood.setRoughness(extractFloat(tf, -Float.MAX_VALUE,
						Float.MAX_VALUE));
			}
		}

		fireUpdate();
		TronRuntime.requestRepaint();
	}
}