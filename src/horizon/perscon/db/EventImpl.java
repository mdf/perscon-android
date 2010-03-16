
package horizon.perscon.db;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import horizon.perscon.model.Constants;
import horizon.perscon.model.Event;
import horizon.perscon.model.EventQuery;
import horizon.perscon.model.EventQueryConstraint;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.Thing;

import static horizon.perscon.Constants.*;

public class EventImpl extends Event
{
	protected static final String TABLE_NAME = "EVENT";

	public static long store(SQLiteDatabase database, Event event)
	{
		if(event.getId()!=Constants.NO_ID)
		{
			return event.getId();
		}
		
		try
		{
			ContentValues values = new ContentValues();
			
			values.put("permissions", event.getPermissions());
			values.put("meta", event.getMeta());
			values.put("eventType", event.getEventType().toString());
			values.put("timestamp", event.getTimestamp());
			values.put("applicationId", event.getApplicationId());
	
			if(event.getPerson()!=null)
			{
				values.put("person", PersonImpl.store(database, event.getPerson()));
			}
			
			if(event.getPlace()!=null)
			{
				values.put("place", PlaceImpl.store(database, event.getPlace()));
			}
			
			if(event.getPlace()!=null)
			{
				values.put("thing", ThingImpl.store(database, event.getThing()));
			}
			
			long id = database.insert(TABLE_NAME, null, values);
			
			event.setId(id);

			UploadDB.notifyNew(database, id, TABLE_NAME);
			
			return id;
		}
		catch(StorageException e)
		{
			throw new StorageException("Error storing Event members", e);				
		}
		catch(SQLException e2)
		{
			throw new StorageException("Error storing Event", e2);	
		}
	}
	
	public static Event [] match(SQLiteDatabase database, EventQuery query)
	{
		try
		{
			String where = null;
			
			if(query!=null && query.getConstraints().size()>0)
			{
				Vector<String> whereParts = new Vector<String>();
				
				for(int i=0; i<query.getConstraints().size(); i++)
				{
					String wherePart;
					
					EventQueryConstraint qc = query.getConstraints().get(i);
					
					switch(qc.getType())
					{
					case EventQuery.PROPERTY_EQ:
						wherePart=qc.getName()+" = '"+qc.getValue().toString()+"'";
						break;
					case EventQuery.PROPERTY_NE:
						wherePart="not " + qc.getName()+" = '"+qc.getValue().toString()+"'";
						break;
					case EventQuery.PROPERTY_IS_NULL:
						wherePart=qc.getName()+" is null ";
						break;
					case EventQuery.PROPERTY_NOT_NULL:
						wherePart="not " + qc.getName()+" is null ";
						break;
					default:
						continue;
					}
					
					whereParts.add(wherePart);
				}
							
				if(whereParts.size()>0)
				{
					where = whereParts.get(0);
					
					for(int i=1;i<whereParts.size();i++)
					{
						where+=" AND " + whereParts.get(i);
					}
				}
			}
			
			debug("where: " + where);
			
			Cursor c = database.query(TABLE_NAME, null, where, null, null, null, null);
			
			if(c!=null)
			{		
				int idColumn = c.getColumnIndex("_ID");
				int permColumn = c.getColumnIndex("permissions");
				int metaColumn = c.getColumnIndex("meta");		
				int typeColumn = c.getColumnIndex("eventType");
				int timestampColumn = c.getColumnIndex("timestamp");
				int personIdColumn = c.getColumnIndex("person");
				int placeIdColumn = c.getColumnIndex("place");
				int thingIdColumn = c.getColumnIndex("thing");
				int appIdColumn = c.getColumnIndex("applicationId");
				
				Event [] es = new Event[c.getCount()];
				int i=0;
				
				if(c.moveToFirst())
				{
					do
					{
						es[i] = new Event();
						es[i].setId(c.getLong(idColumn));
						es[i].setPermissions(c.getInt(permColumn));
						es[i].setMeta(c.getString(metaColumn));
						es[i].setEventType(EventType.valueOf(c.getString(typeColumn)));
						es[i].setTimestamp(c.getLong(timestampColumn));
						es[i].setApplicationId(c.getString(appIdColumn));	
						
						if(c.getLong(personIdColumn)!=Constants.NO_ID)
						{
							Person p = PersonImpl.retrieveUnique(database, c.getLong(personIdColumn));
							es[i].setPerson(p);
						}
						
						if(c.getLong(placeIdColumn)!=Constants.NO_ID)
						{
							Place p = PlaceImpl.retrieveUnique(database, c.getLong(placeIdColumn));
							es[i].setPlace(p);
						}
						
						if(c.getLong(thingIdColumn)!=Constants.NO_ID)
						{
							Thing t = ThingImpl.retrieveUnique(database, c.getLong(thingIdColumn));
							es[i].setThing(t);
						}			
	
						i++;
					}
					while(c.moveToNext());
				}
			
				c.close();
				
				return es;
			}
		}
		catch(StorageException e)
		{
			throw new StorageException("Error getting Event members in match", e);		
		}
		catch(SQLException e2)
		{
			throw new StorageException("Error getting Event in match", e2);			
		}
		
		return null;
	}
	
	public static Event retrieveUnique(SQLiteDatabase database, Long id)
	{
		try
		{
			Cursor c = database.query(TABLE_NAME, null, "_ID = ?", new String [] { id.toString() }, null, null, null);
	
			int idColumn = c.getColumnIndex("_ID");
			int permColumn = c.getColumnIndex("permissions");
			int metaColumn = c.getColumnIndex("meta");
			int typeColumn = c.getColumnIndex("eventType");
			int timestampColumn = c.getColumnIndex("timestamp");
			int personIdColumn = c.getColumnIndex("person");
			int placeIdColumn = c.getColumnIndex("place");
			int thingIdColumn = c.getColumnIndex("thing");
			int appIdColumn = c.getColumnIndex("applicationId");
			
			if(c!=null)
			{
				if(c.moveToFirst())
				{
					Event event = new Event();
					event.setId(c.getLong(idColumn));
					event.setPermissions(c.getInt(permColumn));
					event.setMeta(c.getString(metaColumn));
					event.setEventType(EventType.valueOf(c.getString(typeColumn)));
					event.setTimestamp(c.getLong(timestampColumn));
					event.setApplicationId(c.getString(appIdColumn));
						
					if(c.getLong(personIdColumn)!=Constants.NO_ID)
					{
						Person p = PersonImpl.retrieveUnique(database, c.getLong(personIdColumn));
						event.setPerson(p);
					}
					
					if(c.getLong(placeIdColumn)!=Constants.NO_ID)
					{
						Place p = PlaceImpl.retrieveUnique(database, c.getLong(placeIdColumn));
						event.setPlace(p);
					}
					
					if(c.getLong(thingIdColumn)!=Constants.NO_ID)
					{
						Thing t = ThingImpl.retrieveUnique(database, c.getLong(thingIdColumn));
						event.setThing(t);
					}			
									
					c.close();
					
					return event;
				}
				
				c.close();
			}
		}
		catch(StorageException e)
		{
			throw new StorageException("Error getting Event members" + id.toString(), e);		
		}
		catch(SQLException e2)
		{
			throw new StorageException("Error getting Event " + id.toString(), e2);			
		}
			
		throw new StorageException("Cannot find unique Event " + id.toString());
		
	}
	
	public static void createTable(SQLiteDatabase database)
	{
		debug("create " + TABLE_NAME);
		
		database.execSQL("CREATE TABLE " + TABLE_NAME
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "permissions INTEGER, "
				+ "eventType TEXT, "
				+ "meta TEXT, "
				+ "applicationId TEXT, "
				+ "timestamp BIGINT, "
				+ "person BIGINT, "
				+ "place BIGINT, "
				+ "thing BIGINT);");
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