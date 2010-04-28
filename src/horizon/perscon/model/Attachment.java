
package horizon.perscon.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import horizon.android.logging.Logger;
import horizon.perscon.IDataInputStream;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public class Attachment implements Parcelable 
{
	private static Logger logger = Logger.getLogger(Attachment.class);
	
	protected Long id;

	protected Integer permissions;
	
	protected String meta;
	
	protected String mimeType;
	
	protected byte[] body;
		
	protected Long thingId;
   
	public Attachment()
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

	public String getMeta()
	{
		return meta;
	}

	public void setMeta(String meta)
	{
		this.meta = meta;
	}

	public byte[] getBody()
	{
		return body;
	}

	public void setBody(byte[] body)
	{
		this.body = body;
	}

	
	public String getMimeType()
	{
		return mimeType;
	}

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}
	
	public Long getThingId()
	{
		return thingId;
	}

	public void setThingId(Long thingId)
	{
		this.thingId = thingId;
	}
		
    public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>()
    {
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    private Attachment(Parcel in)
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
		out.writeString(this.mimeType);
		out.writeStrongBinder(new IDataInputStream.Stub()
		{
			private final ByteArrayInputStream in = new ByteArrayInputStream(body);
			public int read(byte[] buffer) throws RemoteException
			{
				try
				{
					return in.read(buffer);
				}
				catch(IOException e)
				{
					logger.error(e.getMessage(), e);
					throw new RuntimeException();
				}
			}
		});
	}

    public void readFromParcel(Parcel in)
    {
    	this.id = in.readLong();
    	this.permissions = in.readInt();
    	this.meta = in.readString();
    	this.mimeType = in.readString();
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
		IDataInputStream data = IDataInputStream.Stub.asInterface(in.readStrongBinder());
    	byte[] buffer = new byte[100];
    	int read;
    	try
		{
			while((read = data.read(buffer)) != -1)
				out.write(buffer, 0, read);
		} 
    	catch(RemoteException e)
		{
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		this.body = out.toByteArray();
    }
}
