package net.skds.lib2.utils.exception;

@SuppressWarnings("unused")
public class StackNotClosedException extends RuntimeException {

	public StackNotClosedException() {

	}

	public StackNotClosedException(String err) {
		super(err);
	}
}
