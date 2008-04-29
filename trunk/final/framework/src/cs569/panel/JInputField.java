package cs569.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * Created on January 26, 2007 
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */

public class JInputField extends JTextField implements FocusListener,
		KeyListener {

	public JInputField() {
		super();

		addFocusListener(this);
		addKeyListener(this);
	}

	public JInputField(String text) {
		super(text);

		addFocusListener(this);
	}

	public JInputField(String text, int columns) {
		super(text, columns);

		addFocusListener(this);
	}

	public void focusGained(FocusEvent e) {
		setSelectionStart(0);
		setSelectionEnd(getText().length());
	}

	public void focusLost(FocusEvent e) {
		fireActionPerformed();
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		fireActionPerformed();
	}

	public void keyTyped(KeyEvent e) {
	}
}