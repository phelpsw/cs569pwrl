package cs569.panel;

import java.awt.event.ItemEvent;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import cs569.material.NormalMappedPhong;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class NormalMappedPhongPanel extends TexturedPhongPanel {
	private JComboBox normalMapCombo;

	public NormalMappedPhongPanel(NormalMappedPhong material) {
		super(material);
	}

	@Override
	public void createContents() {
		super.createContents();
		JLabel label = new JLabel("Normal map :");
		label.setBounds(15, 135, 60, 25);
		add(label);

		normalMapCombo = new JComboBox();
		for (Iterator<String> it = Texture.getTextureList(); it.hasNext(); ) {
			Texture texture = Texture.getTexture(it.next());
			if (texture.getMode() == GL.GL_TEXTURE_2D)
				normalMapCombo.addItem(texture);
		}
		normalMapCombo.setSelectedItem(((NormalMappedPhong)phong).getNormalMapTexture());
		normalMapCombo.setBounds(85, 135, 150, 25);
		normalMapCombo.addItemListener(this);
		add(normalMapCombo);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		((NormalMappedPhong) phong).setNormalMapTexture(((Texture) normalMapCombo.getSelectedItem()));
		super.itemStateChanged(e);
	}
}