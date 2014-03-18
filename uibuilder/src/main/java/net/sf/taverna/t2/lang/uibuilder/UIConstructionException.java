package net.sf.taverna.t2.lang.uibuilder;

/**
 * Used to wrap checked exceptions from the various reflection based UI
 * construction methods
 * 
 * @author Tom Oinn
 */
public class UIConstructionException extends RuntimeException {
	
	private static final long serialVersionUID = 3396809563793962316L;

	public UIConstructionException() {
		//
	}

	public UIConstructionException(String message) {
		super(message);
	}

	public UIConstructionException(Throwable cause) {
		super(cause);
	}

	public UIConstructionException(String message, Throwable cause) {
		super(message, cause);
	}

}
