
package horizon.perscon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import horizon.perscon.model.Constants;
import horizon.perscon.model.Place;

import static horizon.perscon.Constants.*;

public class PlaceImpl extends Place
{
	protected static final String TABLE_NAME = "PLACE";
	
	public static long store(SQLiteDatabase database, Place place)
	{
		if(place.getId()!=Constants.NO_ID)
		{
			return place.getId();
		}
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("permissions", place.getPermissions());
			values.put("meta", place.getMeta());
			values.put("latitude", place.getLatitude());
			values.put("longitude", place.getLongitude());
			values.put("elevation", place.getElevation());
			
			long id = database.insert(TABLE_NAME, null, values);
			
			place.setId(id);
			
			UploadDB.notifyNew(database, id, TABLE_NAME);
			
			return id;
		}
		catch(SQLException e)
		{
			throw new StorageException("Error storing Place", e);	
		}
	}
	
	public static Place retrieveUnique(SQLiteDatabase database, Long id)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "_ID = ?", new String [] { id.toString() }, null, null, null);
	
			int idColumn = c.getColumnIndex("_ID");
			int permColumn = c.getColumnIndex("permissions");
			int metaColumn = c.getColumnIndex("meta");
			int latColumn = c.getColumnIndex("latitude");
			int lonColumn = c.getColumnIndex("longitude");
			int elevColumn = c.getColumnIndex("elevation");
			
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					Place p = new Place();
					p.setId(c.getLong(idColumn));
					p.setPermissions(c.getInt(permColumn));
					p.setMeta(c.getString(metaColumn));
					p.setLatitude(c.getDouble(latColumn));
					p.setLongitude(c.getDouble(lonColumn));
					p.setElevation(c.getDouble(elevColumn));
					
					c.close();
					
					return p;
				}
				
				c.close();
			}
		}
		catch(SQLException e)
		{
			throw new StorageException("Error getting Place " + id.toString(), e);			
		}
			
		throw new StorageException("Cannot find unique Place " + id.toString());
	}
	
	public static void createTable(SQLiteDatabase database)
	{
		debug("create " + TABLE_NAME);
		
		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "permissions INTEGER, "
				+ "meta TEXT, "
				+ "latitude DOUBLE, "
				+ "longitude DOUBLE, "
				+ "elevation DOUBLE);");
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