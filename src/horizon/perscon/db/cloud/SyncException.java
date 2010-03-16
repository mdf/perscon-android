
package horizon.perscon.db.cloud;

public class SyncException extends RuntimeException
{
	private static final long serialVersionUID = -6015689885272281214L;

	public SyncException()
	{
		super();
	}
	 
	public SyncException(String message, Throwable cause)
	{
		super(message, cause);
	}
	 
	public SyncException(String message)
	{
		super(message);
	}
	 
	public SyncException(Throwable cause)
	{
		super(cause);
	}
}