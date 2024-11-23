package net.skds.lib2.mat;

@SuppressWarnings("unused")
public interface IVec {

	int dimension();

	double get(int i);

	float getF(int i);

	int floor(int i);

	int ceil(int i);

}
