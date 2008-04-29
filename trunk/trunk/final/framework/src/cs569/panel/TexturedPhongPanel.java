package cs569.panel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import cs569.apps.TronRuntime;
import cs569.material.TexturedPhong;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class TexturedPhongPanel extends PhongPanel implements ItemListener {
	private JComboBox textureCombo;

	public TexturedPhongPanel(TexturedPhong material) {
		super(material);
	}

	@Override
	public void createContents() {
		super.createContents();
		JLabel label = new JLabel("Texture: ");
		label.setBounds(15, 105, 60, 25);
		add(label);

		textureCombo = new JComboBox();
		for (Iterator<String> it = Texture.getTextureList(); it.hasNext(); ) {
			Texture texture = Texture.getTexture(it.next());
			if (texture.getMode() == GL.GL_TEXTURE_2D)
				textureCombo.addItem(texture);
		}
		textureCombo.setSelectedItem(((TexturedPhong)phong).getDiffuseTexture());
		textureCombo.setBounds(85, 105, 150, 25);
		textureCombo.addItemListener(this);
		add(textureCombo);
	}

	public void itemStateChanged(ItemEvent e) {
		((TexturedPhong) phong).setDiffuseTexture((Texture) textureCombo.getSelectedItem());
		fireUpdate();
		TronRuntime.requestRepaint();
	}
}
