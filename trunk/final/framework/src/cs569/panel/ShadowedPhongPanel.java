package cs569.panel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import cs569.apps.TronRuntime;
import cs569.material.ShadowedPhong;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class ShadowedPhongPanel extends PhongPanel implements ItemListener {
	public ShadowedPhongPanel(ShadowedPhong material) {
		super(material);
	}

	@Override
	public void createContents() {
		super.createContents();
		JLabel label = new JLabel("Shadow map: ");
		label.setBounds(15, 105, 60, 25);
		add(label);

		JComboBox textureList = new JComboBox();
		for (Iterator<String> it = Texture.getTextureList(); it.hasNext(); ) {
			Texture texture = Texture.getTexture(it.next());
			if (texture.getTextureFormat() == GL.GL_DEPTH_COMPONENT)
				textureList.addItem(texture);
		}
		textureList.setSelectedItem(((ShadowedPhong)phong).getShadowMap());
		textureList.setBounds(85, 105, 150, 25);
		textureList.addItemListener(this);
		add(textureList);
	}

	public void itemStateChanged(ItemEvent e) {
		((ShadowedPhong) phong).setShadowMap((Texture) ((JComboBox) e.getSource()).getSelectedItem());
		fireUpdate();
		TronRuntime.requestRepaint();
	}
}
