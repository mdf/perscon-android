package horizon.perscon.db;

import static horizon.perscon.Constants.debug;
import horizon.perscon.model.Application;
import horizon.perscon.model.Constants;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ApplicationImpl
{
	protected static final String TABLE_NAME = "APPLICATION";
	
	public static long store(SQLiteDatabase database, Application application)
	{
		if(application.getId()!=Constants.NO_ID)
		{
			return application.getId();
		}
		
		try
		{
			ContentValues values = new ContentValues();
			values.put("applicationId", application.getApplicationId());
			values.put("name", application.getName());
			values.put("version", application.getVersion());

			long id = database.insert(TABLE_NAME, null, values);
			
			application.setId(id);
						
			UploadDB.notifyNew(database, id, TABLE_NAME);
			
			return id;
		}
		catch(SQLException e)
		{
			throw new StorageException("Error storing Application", e);	
		}
	}
	
	public static Application [] retrieveByApplicationId(SQLiteDatabase database, String appId)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "applicationId = ?", new String [] { appId }, null, null, null);
	
			if(c!=null)
			{
				int idColumn = c.getColumnIndex("_ID");
				int applicationIdColumn = c.getColumnIndex("applicationId");
				int nameColumn = c.getColumnIndex("name");
				int versionColumn = c.getColumnIndex("version");	
				
				Application [] as = new Application[c.getCount()];
				int i=0;
				
				if(c.moveToFirst())
				{
					do
					{
						as[i] = new Application();
						as[i].setId(c.getLong(idColumn));
						as[i].setApplicationId(c.getString(applicationIdColumn));
						as[i].setName(c.getString(nameColumn));
						as[i].setVersion(c.getString(versionColumn));
						
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
			throw new StorageException("Error getting Applications " + appId, e);	
		}

		return null;
	}
	
	public static Application retrieveUnique(SQLiteDatabase database, Long id)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "_ID = ?", new String [] { id.toString() }, null, null, null);

			int idColumn = c.getColumnIndex("_ID");
			int applicationIdColumn = c.getColumnIndex("applicationId");
			int nameColumn = c.getColumnIndex("name");
			int versionColumn = c.getColumnIndex("version");	
	
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					Application a = new Application();
					a.setId(c.getLong(idColumn));
					a.setApplicationId(c.getString(applicationIdColumn));
					a.setName(c.getString(nameColumn));
					a.setVersion(c.getString(versionColumn));
					
					c.close();
					
					return a;
				}
				
				c.close();
			}
		}
		catch(SQLException e)
		{
			throw new StorageException("Error getting Application " + id.toString(), e);			
		}
			
		throw new StorageException("Cannot find unique Application " + id.toString());
	}
	
	public static void createTable(SQLiteDatabase database)
	{		
		debug("create " + TABLE_NAME);

		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "applicationId TEXT, "
				+ "name TEXT,"
				+ "version TEXT);");
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
