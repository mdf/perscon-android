
package horizon.perscon.model;

import java.util.Arrays;
import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

public class EventQuery implements Parcelable
{
	public static final int PROPERTY_EQ = 1;
	public static final int PROPERTY_NE = 2;
	public static final int PROPERTY_IS_NULL = 3;
	public static final int PROPERTY_NOT_NULL = 4;
	
	private Vector<EventQueryConstraint> constraints = new Vector<EventQueryConstraint>();

    public EventQuery()
    {

    }
	
	public Vector<EventQueryConstraint> getConstraints()
	{
		return constraints;
	}

	public void setConstraints(Vector<EventQueryConstraint> constraints)
	{
		this.constraints = constraints;
	}
	
	public void addConstraintEq(String propertyName, Object value)
	{
		EventQueryConstraint eqc = new EventQueryConstraint();
		eqc.setName(propertyName);
		eqc.setType(EventQuery.PROPERTY_EQ);
		eqc.setValue(value);
		
		constraints.add(eqc);
	}
	
	public void addConstraintNe(String propertyName, Object value)
	{
		EventQueryConstraint eqc = new EventQueryConstraint();
		eqc.setName(propertyName);
		eqc.setType(EventQuery.PROPERTY_NE);
		eqc.setValue(value);
		
		constraints.add(eqc);		
	}
	
	public void addConstraintIsNull(String propertyName)
	{
		EventQueryConstraint eqc = new EventQueryConstraint();
		eqc.setName(propertyName);
		eqc.setType(EventQuery.PROPERTY_IS_NULL);
		
		constraints.add(eqc);			
	}
	
	public void addConstraintNotNull(String propertyName)
	{
		EventQueryConstraint eqc = new EventQueryConstraint();
		eqc.setName(propertyName);
		eqc.setType(EventQuery.PROPERTY_NOT_NULL);
		
		constraints.add(eqc);			
	}
	
    public static final Parcelable.Creator<EventQuery> CREATOR = new Parcelable.Creator<EventQuery>()
    {
        public EventQuery createFromParcel(Parcel in) {
            return new EventQuery(in);
        }

        public EventQuery[] newArray(int size) {
            return new EventQuery[size];
        }
    };

    private EventQuery(Parcel in)
    {
        readFromParcel(in);
    }
	
	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeParcelableArray(constraints.toArray(new EventQueryConstraint[constraints.size()]), 0);
	}

    @SuppressWarnings("unchecked")
	public void readFromParcel(Parcel in)
    {
    	this.constraints = new Vector(Arrays.asList(in.readParcelableArray(this.getClass().getClassLoader())));
    }
}
