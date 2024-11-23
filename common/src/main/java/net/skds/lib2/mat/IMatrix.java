package net.skds.lib2.mat;

public interface IMatrix {

	int width();

	int height();

	double get(int row, int col);

	float getF(int row, int col);

	double[] getRow(int row);

	float[] getRowF(int row);

	IVec getRowV(int row);

	double[] getCol(int row);

	float[] getColF(int row);

	IVec getColV(int row);
}
