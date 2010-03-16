
package horizon.perscon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable
{
	protected Long id;
	
	protected Integer permissions;
	
	protected String meta;
	
	protected String name;

    public Person()
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>()
    {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    private Person(Parcel in)
    {
        readFromParcel(in);
    }
	
	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeLong(this.id);
		out.writeInt(this.permissions);
		out.writeString(this.meta);
		out.writeString(this.name);
	}

    public void readFromParcel(Parcel in)
    {
    	this.id = in.readLong();
    	this.permissions = in.readInt();
    	this.meta = in.readString();
    	this.name = in.readString();
    }	
}