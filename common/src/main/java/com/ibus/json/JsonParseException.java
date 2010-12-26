package com.ibus.json;

@SuppressWarnings("serial")
public class JsonParseException extends RuntimeException {

	public JsonParseException() {
		super();
	}

	public JsonParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonParseException(String message) {
		super(message);
	}

	public JsonParseException(Throwable cause) {
		super(cause);
	}

}
