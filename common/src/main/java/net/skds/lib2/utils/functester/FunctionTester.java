package net.skds.lib2.utils.functester;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.utils.functester.api.ITestFunction;
import net.skds.lib2.utils.functester.gui.Frame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FunctionTester {

	//*
	public static void main(String[] args) {
		FunctionTester tester = new FunctionTester();
		//*
		tester.addFunction(new ITestFunction() {
			@Override
			public double calc(float input) {
				if (input <= 0) return 0;
				input /= 15f;
				return FastMath.invSqrt(input) * 2;
			}

			@Override
			public Color color() {
				return Color.BLUE;
			}
		});
		tester.addFunction(new ITestFunction() {
			@Override
			public double calc(float input) {
				if (input <= 0) return 0;
				input /= 15f;
				return (float) (FastMath.invSqrt(input) - 1 / Math.sqrt(input)) * 100_000;
			}

			@Override
			public Color color() {
				return Color.MAGENTA;
			}
		});
		tester.addFunction(new ITestFunction() {
			@Override
			public double calc(float input) {
				if (input <= 0) return 0;
				input /= 15f;
				return (float) (FastMath.invSqrt((double) input) - 1 / Math.sqrt(input)) * 1_000_000_000;
			}

			@Override
			public Color color() {
				return Color.RED;
			}
		});
		//tester.addFunction(new ITestFunction() {
		//	@Override
		//	public double calc(float input) {	
		//		int val = (int) input + 1000;
		//		val *= 2;	
		//		if (val < 0 || val >= dat.length - 1) {
		//			return 0;
		//		}	
		//		double dd = dat[val] / 100;
		//		return Math.abs(dd) > .1 ? dd : 0;
		//	}	
		//	@Override
		//	public Color color() {
		//		return Color.RED;
		//	}
		//});
	}
	//*/

	public Frame frame;
	public List<ITestFunction> functions = new ArrayList<>();

	public int pps = 100;

	public float xScale = 1.0F / 200;
	public float yScale = 1.0F;
	public int steps = 5;

	public FunctionTester() {
		this.frame = new Frame(this);
	}

	public void addFunction(ITestFunction function) {
		functions.add(function);
	}

	public int genValue(ITestFunction function, int point) {

		float x = (float) point / (pps * xScale);
		double y = function.calc(x);

		return (int) (y * yScale * pps);
	}

}
