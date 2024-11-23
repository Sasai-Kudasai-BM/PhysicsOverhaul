package net.skds.lib2.utils.functester.gui;

import net.skds.lib2.utils.functester.FunctionTester;

import javax.swing.*;


public class Frame extends JFrame {

	public final FunctionTester tester;
	public final GraphPad pad;

	public Frame(FunctionTester tester) {
		super();
		this.pad = new GraphPad(this);
		add(pad);
		this.tester = tester;
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(1600, 800);
		setVisible(true);
	}
}
