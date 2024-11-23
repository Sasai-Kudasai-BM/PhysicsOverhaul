package net.skds.lib2.utils.functester.api;

import java.awt.*;

public interface ITestFunction {

	public static final Color DEF_COLOR = Color.BLUE;

	public double calc(float input);

	default Color color() {
		return DEF_COLOR;
	}
}
