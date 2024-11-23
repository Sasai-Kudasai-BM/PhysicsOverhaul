package net.skds.lib2.utils.functester.gui;

import net.skds.lib2.utils.functester.api.ITestFunction;

import javax.swing.*;
import java.awt.*;

public class GraphPad extends JPanel {

	private Color bg = new Color(255, 255, 255);
	private Color smallGrid = new Color(200, 200, 200);
	private Color grid = new Color(0, 0, 0);
	//private Timer timer;

	private final Frame frame;

	public GraphPad(Frame frame) {
		this.frame = frame;
		setBackground(bg);
		//timer = new Timer(100, new ActionListener() {
		//	@Override
		//	public void actionPerformed(ActionEvent e) {
		//		repaint();
		//	}
		//});
		//timer.setInitialDelay(0);
		//timer.setRepeats(true);
		//timer.setCoalesce(true);
		//timer.start();

		setVisible(true);
		setBounds(0, 0, 1600, 800);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawGrid(g);
		for (ITestFunction function : frame.tester.functions) {
			drawFunc(g, function);
		}
	}

	private void drawGrid(Graphics g) {

		int h = getHeight();
		int w = getWidth();
		int xm = w / 2;
		int ym = h / 2;
		int step = frame.tester.pps / frame.tester.steps;
		g.setColor(smallGrid);
		for (int i = xm + step; i < w; i += step) {
			drawLineY(g, i);
		}
		for (int i = xm - step; i > 0; i -= step) {
			drawLineY(g, i);
		}
		for (int i = ym + step; i < h; i += step) {
			drawLineX(g, i);
		}
		for (int i = ym - step; i > 0; i -= step) {
			drawLineX(g, i);
		}

		g.setColor(grid);
		drawLineX(g, ym);
		drawLineY(g, xm);
	}

	private void drawFunc(Graphics g, ITestFunction function) {

		int h = getHeight();
		int w = getWidth();
		int xm = w / 2;
		int ym = h / 2;

		g.setColor(function.color());
		int yPre = frame.tester.genValue(function, -xm) - ym;
		for (int x = 1; x < w; x++) {
			int y = frame.tester.genValue(function, x - xm) - ym;
			g.drawLine(x, -yPre, x, -y);
			yPre = y;
		}

	}

	private void drawLineX(Graphics g, int cord) {
		g.drawLine(0, cord, getWidth(), cord);
	}

	private void drawLineY(Graphics g, int cord) {
		g.drawLine(cord, 0, cord, getHeight());
	}
}
