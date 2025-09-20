package core.integrationcontrollers.servicegateway.sg.mapping;

public class MappingException extends Exception {
	private static final long serialVersionUID = 5350290964520582282L;
	
	private final String message;
	private String exceptionId = "1203"; // Por defecto utilizamos esta excepcion: Client.NotFound
	
	public MappingException(String message) {
		super();
		this.message = message;
	}

	public MappingException(String message, Exception e) {
		super(e);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}
	public String getExceptionId() {
		return exceptionId;
	}
}
