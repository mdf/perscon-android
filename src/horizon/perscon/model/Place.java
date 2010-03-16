
package horizon.perscon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable
{
	protected Long id;
	
	protected Integer permissions;
	
	protected String meta;
	
	protected Double latitude;
	
	protected Double longitude;
	
	protected Double elevation;

    public Place()
    {
    	this.id = Constants.NO_ID;
		this.permissions = Constants.NO_PERMISSIONS;
    }
    	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public int getPermissions()
	{
		return permissions;
	}

	public void setPermissions(int permissions)
	{
		this.permissions = permissions;
	}

	public String getMeta()
	{
		return meta;
	}

	public void setMeta(String meta)
	{
		this.meta = meta;
	}

	public Double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public Double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public Double getElevation()
	{
		return elevation;
	}

	public void setElevation(double elevation)
	{
		this.elevation = elevation;
	}
	
    private Place(Parcel in)
    {
        readFromParcel(in);
    }
    
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>()
    {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    
	public int describeContents()
	{
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeLong(this.id);
		out.writeInt(this.permissions);
		out.writeString(this.meta);
		out.writeDouble(this.latitude);
		out.writeDouble(this.longitude);
		out.writeDouble(this.elevation);
	}

    public void readFromParcel(Parcel in)
    {
    	this.id = in.readLong();
    	this.permissions = in.readInt();
    	this.meta = in.readString();
    	this.latitude = in.readDouble();
    	this.longitude = in.readDouble();
    	this.elevation = in.readDouble();
    }	
}
