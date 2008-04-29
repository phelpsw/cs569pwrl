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
import cs569.material.Reflection;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */

public class ReflectionPanel extends MaterialPanel implements ItemListener {
	Reflection reflection;

	public ReflectionPanel(Reflection material) {
		baseMat = material;
		reflection = material;
		createContents();
	}

	public void createContents() {
		JLabel label;
		label = new JLabel("Texture: ");
		label.setBounds(15, 15, 60, 25);
		add(label);

		JComboBox textureList = new JComboBox();
		for (Iterator<String> it = Texture.getTextureList(); it.hasNext(); ) {
			Texture texture = Texture.getTexture(it.next());
			if (texture.getMode() == GL.GL_TEXTURE_CUBE_MAP)
				textureList.addItem(texture);
		}
		
		textureList.setSelectedItem(reflection.getTexture());
		textureList.setBounds(85, 15, 150, 25);
		textureList.addItemListener(this);
		add(textureList);
	}

	public void itemStateChanged(ItemEvent e) {
		reflection.setTexture((Texture) ((JComboBox) e.getSource()).getSelectedItem());
		fireUpdate();
		TronRuntime.requestRepaint();
	}
	
	public void stateChanged(ChangeEvent e) {
	}
	
	public void actionPerformed(ActionEvent e) {
	}
}