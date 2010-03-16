
package horizon.perscon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import horizon.perscon.model.Attachment;
import horizon.perscon.model.Constants;

import static horizon.perscon.Constants.*;

public class AttachmentImpl extends Attachment
{
	protected static final String TABLE_NAME = "ATTACHMENT";
	
	public static long store(SQLiteDatabase database, Attachment attachment)
	{
		if(attachment.getId()!=Constants.NO_ID)
		{
			return attachment.getId();
		}
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("permissions", attachment.getPermissions());
			values.put("meta", attachment.getMeta());
			values.put("thingId", attachment.getThingId());
			values.put("body", attachment.getBody());

			long id = database.insert(TABLE_NAME, null, values);
			
			attachment.setId(id);
						
			UploadDB.notifyNew(database, id, TABLE_NAME);
			
			return id;
		}
		catch(SQLException e)
		{
			throw new StorageException("Error storing Attachment", e);	
		}
	}
	
	public static Attachment [] retrieveByThingId(SQLiteDatabase database, Long thingId)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "thingId = ?", new String [] { thingId.toString() }, null, null, null);
	
			if(c!=null)
			{
				int idColumn = c.getColumnIndex("_ID");
				int permColumn = c.getColumnIndex("permissions");
				int metaColumn = c.getColumnIndex("meta");		
				int thingIdColumn = c.getColumnIndex("thingId");
				int bodyColumn = c.getColumnIndex("body");
				
				Attachment [] as = new Attachment[c.getCount()];
				int i=0;
				
				if(c.moveToFirst())
				{
					do
					{
						as[i] = new Attachment();
						as[i].setId(c.getLong(idColumn));
						as[i].setPermissions(c.getInt(permColumn));
						as[i].setMeta(c.getString(metaColumn));
						as[i].setThingId(c.getLong(thingIdColumn));
						as[i].setBody(c.getBlob(bodyColumn));		
						
						i++;
					}
					while(c.moveToNext());
				}
			
				c.close();
				
				return as;
			}
				
		}
		catch(SQLException e)
		{
			throw new StorageException("Error getting Attachments " + thingId.toString(), e);	
		}
		
		return null;
	}
	
	public static Attachment retrieveUnique(SQLiteDatabase database, Long id)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "_ID = ?", new String [] { id.toString() }, null, null, null);
	
			int idColumn = c.getColumnIndex("_ID");
			int permColumn = c.getColumnIndex("permissions");
			int metaColumn = c.getColumnIndex("meta");		
			int thingIdColumn = c.getColumnIndex("thingId");
			int bodyColumn = c.getColumnIndex("body");
	
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					Attachment a = new Attachment();
					a.setId(c.getLong(idColumn));
					a.setPermissions(c.getInt(permColumn));
					a.setMeta(c.getString(metaColumn));
					a.setThingId(c.getLong(thingIdColumn));
					a.setBody(c.getBlob(bodyColumn));
					
					c.close();
					
					return a;
				}
				
				c.close();
			}
		}
		catch(SQLException e)
		{
			throw new StorageException("Error getting Attachment " + id.toString(), e);			
		}
			
		throw new StorageException("Cannot find unique Attachment " + id.toString());
	}
	
	public static void createTable(SQLiteDatabase database)
	{		
		debug("create " + TABLE_NAME);
		
		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "permissions INTEGER, "
				+ "meta TEXT,"
				+ "thingId BIGINT,"
				+ "body BLOB);");
	}
	
	public static void recreateTable(SQLiteDatabase database)
	{
		debug("recreate" + TABLE_NAME);
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		createTable(database);		
	}

	public static String getTableName()
	{
		return TABLE_NAME;
	}
}