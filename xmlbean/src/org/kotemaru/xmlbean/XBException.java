package org.kotemaru.xmlbean;

public class XBException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XBException(String msg) {
		super(msg);
	}

	public XBException(Throwable t) {
		super(t);
	}
}
