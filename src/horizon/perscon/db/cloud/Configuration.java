package horizon.perscon.db.cloud;

public class Configuration
{
	private boolean https;
	 
	private int port;
	  
	private String host;
	 
	public boolean isHttps()
	{
		return https;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setHttps(boolean https)
	{
		this.https = https;
	}
}
