
package horizon.perscon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import horizon.perscon.model.Constants;
import horizon.perscon.model.Thing;

import static horizon.perscon.Constants.*;


public class ThingImpl extends Thing
{
	protected static final String TABLE_NAME = "THING";
	
	public static long store(SQLiteDatabase database, Thing thing)
	{
		if(thing.getId()!=Constants.NO_ID)
		{
			return thing.getId();
		}
		
		try
		{		
			ContentValues values = new ContentValues();
			values.put("permissions", thing.getPermissions());
			values.put("meta", thing.getMeta());
			values.put("origin", thing.getOrigin());
			
			long id = database.insert(TABLE_NAME, null, values);

			thing.setId(id);
			
			UploadDB.notifyNew(database, id, TABLE_NAME);
			
			if(thing.getAttachments()!=null)
			{
				for(int i=0; i<thing.getAttachments().length; i++)
				{
					thing.getAttachments()[i].setThingId(thing.getId());
					AttachmentImpl.store(database, thing.getAttachments()[i]);
				}
			}
			
			return id;				
		}
		catch(StorageException e)
		{
			throw new StorageException("Error storing Thing-Attachments", e);				
		}
		catch(SQLException e2)
		{
			throw new StorageException("Error storing Thing", e2);	
		}
	}
	
	public static Thing retrieveUnique(SQLiteDatabase database, Long id)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "_ID = ?", new String [] { id.toString() }, null, null, null);
	
			int idColumn = c.getColumnIndex("_ID");
			int permColumn = c.getColumnIndex("permissions");
			int metaColumn = c.getColumnIndex("meta");		
			int originColumn = c.getColumnIndex("origin");
			
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					Thing t = new Thing();
					t.setId(c.getLong(idColumn));
					t.setPermissions(c.getInt(permColumn));
					t.setMeta(c.getString(metaColumn));
					t.setOrigin(c.getString(originColumn));
					
					c.close();
					
					t.setAttachments(AttachmentImpl.retrieveByThingId(database, t.getId()));
					
					return t;
				}
				
				c.close();
			}
		}
		catch(StorageException e)
		{
			throw new StorageException("Error getting Thing-Attachments" + id.toString(), e);		
		}
		catch(SQLException e2)
		{
			throw new StorageException("Error getting Thing " + id.toString(), e2);			
		}
			
		throw new StorageException("Cannot find unique Thing " + id.toString());
	}
	
	public static void createTable(SQLiteDatabase database)
	{
		debug("create " + TABLE_NAME);
		
		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "permissions INTEGER, "
				+ "meta TEXT, "
				+ "origin TEXT);");
	}
	
	public static void recreateTable(SQLiteDatabase database)
	{
		debug("recreate " + TABLE_NAME);
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		createTable(database);		
	}

	public static String getTableName()
	{
		return TABLE_NAME;
	}
}