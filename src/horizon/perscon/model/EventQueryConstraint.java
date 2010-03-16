package horizon.perscon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventQueryConstraint implements Parcelable
{	
	private String name;
	
	private Object value;
	
	private int type;
	
	public EventQueryConstraint()
	{
		
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
	
	
    public static final Parcelable.Creator<EventQueryConstraint> CREATOR = new Parcelable.Creator<EventQueryConstraint>()
    {
        public EventQueryConstraint createFromParcel(Parcel in) {
            return new EventQueryConstraint(in);
        }

        public EventQueryConstraint[] newArray(int size) {
            return new EventQueryConstraint[size];
        }
    };

    private EventQueryConstraint(Parcel in)
    {
        readFromParcel(in);
    }
	
	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(this.name);
		out.writeInt(this.type);
		out.writeString(this.value.toString());
	}
	

    public void readFromParcel(Parcel in)
    {
    	this.name = in.readString();
    	this.type = in.readInt();
    	this.value = in.readString();
    }
}
