
package horizon.perscon.db.cloud.model;

public class User
{
	//{ "ctime": "1268517492.663168", "desc": null, "uid": "301af54f12bdfa63aad40fb2aa59dd1f29f10a9f",
	// "name": null, "user": null }
	
	private float ctime;
	
	private String desc;
	
	private String uid;
	
	private String name;
	
	private String user;

	public float getCtime()
	{
		return ctime;
	}

	public void setCtime(float ctime)
	{
		this.ctime = ctime;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}
}
