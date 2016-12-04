package es.uvigo.ei.sing.pandrugsdb.util;

public class WrapperRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WrapperRuntimeException(Throwable cause) {
		super(cause);
	}

	@SuppressWarnings("unchecked")
	public <T extends Throwable> T unwrap() {
		return (T) this.getCause();
	}
}
