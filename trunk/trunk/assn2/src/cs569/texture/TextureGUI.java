/**
 * Created on Feb 11, 2006
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package cs569.texture;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class TextureGUI implements ListSelectionListener, ActionListener {
	protected Component parent;
	protected JList list;
	protected DefaultListModel listModel;
	protected Texture tex;
	protected JDialog loadDialog;

	public TextureGUI(Component parent) {
		this.parent = parent;
	}

	public Texture getTexture() {
		loadDialog = new JDialog((Frame) SwingUtilities.getAncestorOfClass(
				Frame.class, parent));
		loadDialog.setModal(true);
		JPanel temp = new JPanel(new BorderLayout());
		
		listModel = new DefaultListModel();
		List<String> nameList = Texture.getSortedTextureList();
		for (int i=0; i<nameList.size(); i++)
			listModel.addElement(nameList.get(i));

		// Create the list and put it in a scroll pane.
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		list.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(list);

		JButton load = new JButton("Load ..");
		load.setActionCommand("texLoad");
		load.addActionListener(this);

		JButton set = new JButton("Preview");
		set.setActionCommand("texSet");
		set.addActionListener(this);

		// Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(load);
		buttonPane.add(set);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		temp.add(listScrollPane, BorderLayout.CENTER);
		temp.add(buttonPane, BorderLayout.PAGE_END);

		loadDialog.add(temp);
		loadDialog.setSize(500, 400);
		loadDialog.setLocation(200, 200);
		loadDialog.setVisible(true);
		return tex;
	}

	public String loadImageDialog() {
		final JFileChooser fc = new JFileChooser(ClassLoader.getSystemResource("").getPath());
		fc.addChoosableFileFilter(new ImageFilter());
		int returnVal = fc.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fc.getSelectedFile();
				return (file.getAbsolutePath());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(parent,
						"Error: Could not load texture.", "Texture Loader",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		return null;
	}

	public void valueChanged(ListSelectionEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("texLoad")) {
			String name = loadImageDialog();
			if (name != null) {
				Texture.getTexture(name);
				listModel.addElement(name);
				list.setSelectedIndex(listModel.getSize() - 1);
			}
		} else if (cmd.equals("texSet")) {
			int index = list.getSelectedIndex();
			if (index == -1) {
				return;
			}
			String name = (String) listModel.get(index);
			try {
				tex = Texture.getTexture(name);
			} catch (UndefinedTextureException exception) {
				exception.printStackTrace();
			}
			loadDialog.dispose();
		}
	}

	protected class ImageFilter extends FileFilter {
		private final String tiff = "tiff";
		private final String tif = "tif";
		private final String jpeg = "jpeg";
		private final String jpg = "jpg";
		private final String png = "png";

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null) {
				if (extension.equalsIgnoreCase(tiff)
						|| extension.equalsIgnoreCase(tif)
						|| extension.equalsIgnoreCase(jpeg)
						|| extension.equalsIgnoreCase(jpg)
						|| extension.equalsIgnoreCase(png)) {
					return true;
				}
			}
			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}

		@Override
		public String getDescription() {
			return "Image Files";
		}
	}

}
