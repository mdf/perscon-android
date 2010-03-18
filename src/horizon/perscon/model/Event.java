
package horizon.perscon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable
{
	public static enum EventType
	{
		CREATED,
		OBSERVED,
		MODIFIED,
		DELETED
	}
	    
	public Event()
	{
		this.id = Constants.NO_ID;
		this.permissions = Constants.NO_PERMISSIONS;
	}
	
	protected Long id;

	protected Integer permissions;
	
	protected String meta;	

	protected String applicationId;
	
	protected EventType eventType;
	
	protected Place place;
	
	protected Person person;
	
	protected Thing thing;
	
	protected Long timestamp;
	
	protected PrivacyMask privacyMask;

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

	public String getMeta()
	{
		return meta;
	}

	public void setMeta(String meta)
	{
		this.meta = meta;
	}	
	
	public String getApplicationId()
	{
		return applicationId;
	}

	public void setApplicationId(String applicationId)
	{
		this.applicationId = applicationId;
	}
	
	public EventType getEventType()
	{
		return eventType;
	}

	public void setEventType(EventType eventType)
	{
		this.eventType = eventType;
	}

	public Place getPlace()
	{
		return place;
	}

	public void setPlace(Place place)
	{
		this.place = place;
	}

	public Person getPerson()
	{
		return person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	public Thing getThing()
	{
		return thing;
	}

	public void setThing(Thing thing)
	{
		this.thing = thing;
	}

	public Long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public PrivacyMask getPrivacyMask()
	{
		return privacyMask;
	}

	public void setPrivacyMask(PrivacyMask privacyMask)
	{
		this.privacyMask = privacyMask;
	}

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>()
    {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    private Event(Parcel in)
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
		out.writeString(this.applicationId);
		out.writeString(this.eventType.toString());
		out.writeLong(this.timestamp);
		out.writeParcelable(this.person, 0);
		out.writeParcelable(this.place, 0);
		out.writeParcelable(this.thing, 0);		
		out.writeParcelable(this.privacyMask, 0);		
	}

    public void readFromParcel(Parcel in)
    {
    	this.id = in.readLong();
    	this.permissions = in.readInt();
    	this.meta = in.readString();
    	this.applicationId = in.readString();
    	this.eventType = EventType.valueOf(in.readString());
    	this.timestamp = in.readLong();
    	this.person = in.readParcelable(null);
    	this.place = in.readParcelable(null);
    	this.thing = in.readParcelable(null);
    	this.privacyMask = in.readParcelable(null);
    }

}