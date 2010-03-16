
package horizon.perscon.db;

public class StorageException extends RuntimeException
{
	private static final long serialVersionUID = -6805442603100666141L;

	public StorageException()
	{
		super();
	}
	 
	public StorageException(String message, Throwable cause)
	{
		super(message, cause);
	}
	 
	public StorageException(String message)
	{
		super(message);
	}
	 
	public StorageException(Throwable cause)
	{
		super(cause);
	}
}