package horizon.perscon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Application implements Parcelable
{	
	protected Long id;
	
	protected String applicationId;
	
	protected String name;
	
	protected String version;
	
	public Application()
	{
		this.id = Constants.NO_ID;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getApplicationId()
	{
		return applicationId;
	}

	public void setApplicationId(String applicationId)
	{
		this.applicationId = applicationId;
	}	

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}
	
    private Application(Parcel in)
    {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Application> CREATOR = new Parcelable.Creator<Application>()
    {
        public Application createFromParcel(Parcel in) {
            return new Application(in);
        }

        public Application[] newArray(int size) {
            return new Application[size];
        }
    };
    
	public int describeContents()
	{
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeLong(this.id);
		out.writeString(this.applicationId);
		out.writeString(this.name);
		out.writeString(this.version);
	}

    public void readFromParcel(Parcel in)
    {
    	this.id = in.readLong();
    	this.applicationId = in.readString();
    	this.name = in.readString();
    	this.version = in.readString();
    }
}
