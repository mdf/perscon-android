
package horizon.perscon.db.cloud.model;

public class Application
{
	/*
	"aid": "app-2", 
    "version": "0.1", 
    "cfunc": null, 
    "user": "f26292635be73a45c746a58a7e07384dafdcb31b", 
    "name": "TestApp"
    */
	
	private String aid;
	
	private String version;
	
	private String cfunc;
	
	private String user;
	
	private String name;

	public String getAid()
	{
		return aid;
	}

	public void setAid(String aid)
	{
		this.aid = aid;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getCfunc()
	{
		return cfunc;
	}

	public void setCfunc(String cfunc)
	{
		this.cfunc = cfunc;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
  }
