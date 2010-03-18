
package horizon.perscon.model;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Thing implements Parcelable
{
	protected Long id;

	protected Integer permissions;
	
	protected String origin;
	
	protected String meta;
	
	protected Attachment [] attachments;
	    
    public Thing()
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
	
	public Integer getPermissions()
	{
		return permissions;
	}

	public void setPermissions(Integer permissions)
	{
		this.permissions = permissions;
	}
	
	public String getOrigin()
	{
		return origin;
	}

	public void setOrigin(String origin)
	{
		this.origin = origin;
	}

	public String getMeta()
	{
		return meta;
	}

	public void setMeta(String meta)
	{
		this.meta = meta;
	}
	
	public Attachment[] getAttachments()
	{
		return attachments;
	}

	public void setAttachments(Attachment[] attachments)
	{
		this.attachments = attachments;
	}	
	
    public static final Parcelable.Creator<Thing> CREATOR = new Parcelable.Creator<Thing>()
    {
        public Thing createFromParcel(Parcel in) {
            return new Thing(in);
        }

        public Thing[] newArray(int size) {
            return new Thing[size];
        }
    };
    
    private Thing(Parcel in)
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
		out.writeString(this.origin);
		out.writeParcelableArray(this.attachments, 0);
	}

    public void readFromParcel(Parcel in)
    {
    	this.id = in.readLong();
    	this.permissions = in.readInt();
    	this.meta = in.readString();
    	this.origin = in.readString();
    	
    	List<Parcelable> ps = Arrays.asList(in.readParcelableArray(this.getClass().getClassLoader()));
    	this.attachments = new Attachment[ps.size()];
    	ps.toArray(this.attachments);
    }


}