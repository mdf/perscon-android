
package horizon.perscon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static horizon.perscon.Constants.*;

public class UploadDB
{
	protected static final String TABLE_NAME = "UPLOAD";
	
	protected Long id;
	
	protected String type;
	
	protected Integer uploaded;
	
	protected Long timestamp;
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Integer getUploaded()
	{
		return uploaded;
	}

	public void setUploaded(Integer uploaded)
	{
		this.uploaded = uploaded;
	}
	

	public Long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public static void notifyNew(SQLiteDatabase database, Long id, String type)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put("id", id.toString());
			values.put("type", type);
			values.put("uploaded", 0);
			values.put("timestamp", System.currentTimeMillis());
			
			database.insert(TABLE_NAME, null, values);
		}
		catch(SQLException e)
		{
			throw new StorageException("Error storing Upload record", e);	
		}
	}
	
	public static void notifyUploaded(SQLiteDatabase database, UploadDB u)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put("uploaded", 1);
			
			database.update(TABLE_NAME, values, "id = ? AND type = ?", new String [] { u.getId().toString(), u.getType()});
		}
		catch(SQLException e)
		{
			throw new StorageException("Error updating Upload record", e);	
		}
	}
	
	public static UploadDB [] getToUpload(SQLiteDatabase database)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "uploaded = 0", null, null, null, "timestamp");
	
			if(c!=null)
			{
				int idColumn = c.getColumnIndex("id");
				int typeColumn = c.getColumnIndex("type");
				int uploadedColumn = c.getColumnIndex("uploaded");	
				int timestampColumn = c.getColumnIndex("timestamp");	
				
				UploadDB [] us = new UploadDB[c.getCount()];
				int i=0;
				
				if(c.moveToFirst())
				{
					do
					{
						us[i] = new UploadDB();
						us[i].setId(c.getLong(idColumn));
						us[i].setType(c.getString(typeColumn));
						us[i].setUploaded(c.getInt(uploadedColumn));
						us[i].setTimestamp(c.getLong(timestampColumn));
						
						i++;
					}
					while(c.moveToNext());
				}
			
				c.close();
				
				return us;
			}
				
		}
		catch(SQLException e)
		{
			throw new StorageException("Error getting waiting Uploads", e);	
		}		
		
		return null;
	}
	
	public static void createTable(SQLiteDatabase database)
	{
		debug("create " + TABLE_NAME);
		
		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(id BIGINT, "
				+ "type TEXT, "
				+ "uploaded INT, "
				+ "timestamp BIGINT);");
	}
	
	public static void recreateTable(SQLiteDatabase database)
	{
		debug("recreate " + TABLE_NAME);
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		createTable(database);		
	}

}
