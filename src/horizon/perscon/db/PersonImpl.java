
package horizon.perscon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import horizon.perscon.model.Constants;
import horizon.perscon.model.Person;

import static horizon.perscon.Constants.*;

public class PersonImpl extends Person
{
	protected static final String TABLE_NAME = "PERSON";

	public static long store(SQLiteDatabase database, Person p)
	{
		if(p.getId()!=Constants.NO_ID)
		{
			return p.getId();
		}
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("permissions", p.getPermissions());
			values.put("meta", p.getMeta());
			values.put("name", p.getName());
			
			long id = database.insert(TABLE_NAME, null, values);

			p.setId(id);
			
			UploadDB.notifyNew(database, id, TABLE_NAME);
			
			return id;
		}
		catch(SQLException e)
		{
			throw new StorageException("Error storing Person", e);	
		}
		
	}
	
	public static Person retrieveUnique(SQLiteDatabase database, Long id)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "_ID = ?", new String [] { id.toString() }, null, null, null);
	
			int idColumn = c.getColumnIndex("_ID");
			int permColumn = c.getColumnIndex("permissions");
			int metaColumn = c.getColumnIndex("meta");		
			int nameColumn = c.getColumnIndex("name");
		
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					Person p = new Person();
					p.setId(c.getLong(idColumn));
					p.setPermissions(c.getInt(permColumn));
					p.setMeta(c.getString(metaColumn));
					p.setName(c.getString(nameColumn));
					
					c.close();
					
					return p;
				}
				
				c.close();
			}
		}
		catch(SQLException e)
		{
			throw new StorageException("Error getting Person " + id.toString(), e);			
		}
			
		throw new StorageException("Cannot find unique Person " + id.toString());
	}
	
	public static void createTable(SQLiteDatabase database)
	{
		debug("create " + TABLE_NAME);
		
		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "permissions INTEGER, "
				+ "meta TEXT, "
				+ "name TEXT);");
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