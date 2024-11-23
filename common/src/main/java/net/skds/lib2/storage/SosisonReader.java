package net.skds.lib2.storage;

public interface SosisonReader {

	String readString();

	int readInt();

	long readLong();

	float readFloat();

	double readDouble();

	void beginObject();

	void endObject();
}
