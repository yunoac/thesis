package exceptions;

public class IndexedAccessException extends RuntimeException {

	private static final long serialVersionUID = -3148680743422833877L;
	
	public IndexedAccessException(String errorMessage) {
	    super(errorMessage);
	}
	
	public IndexedAccessException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}

}
