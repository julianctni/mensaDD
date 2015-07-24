package com.pasta.mensadd.guthaben;
/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class DesfireException extends Exception {

	private static final long serialVersionUID = 1649606588619861201L;
	public DesfireException(String message) {
		super(message);
	}
	public DesfireException(Throwable cause) {
		super(cause);
	}
}
