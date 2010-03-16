
package horizon.perscon.db.cloud;

public class RegistrationException extends RuntimeException
{
	private static final long serialVersionUID = -6805442603100666141L;

	public RegistrationException()
	{
		super();
	}
	 
	public RegistrationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	 
	public RegistrationException(String message)
	{
		super(message);
	}
	 
	public RegistrationException(Throwable cause)
	{
		super(cause);
	}
}