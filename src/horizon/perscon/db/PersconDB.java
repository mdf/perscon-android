
package horizon.perscon.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import horizon.perscon.IPersconServiceCallback;
import horizon.perscon.db.cloud.CloudClient;
import horizon.perscon.db.cloud.Configuration;
import horizon.perscon.db.cloud.DefaultPaths;
import horizon.perscon.db.cloud.Paths;
import horizon.perscon.db.cloud.RegistrationException;

import horizon.perscon.model.Event;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.PrivacyMask;
import horizon.perscon.model.Thing;
import horizon.perscon.model.EventQuery;
import horizon.perscon.model.Event.EventType;

import static horizon.perscon.Constants.*;

public class PersconDB
{
	private final static String DATABASE_NAME = "persconDB";
	private final static int DATABASE_VERSION = 1;

	private SQLiteDatabase database = null;
	
	private Context context;
	
	private CloudClient cloudClient;
	
	public PersconDB(Context context)
	{
		this.context = context;
		
		DatabaseHelper dbh = new DatabaseHelper(this.context);
		this.database = dbh.getWritableDatabase();		
		
		Paths paths = new DefaultPaths();
		
		Configuration config = new Configuration();
		
		//config.setHttps(false);
		//config.setPort(8080);
		//config.setHost("192.168.0.149");
		//config.setHost("128.243.35.10");
		
		config.setHttps(false);
		config.setPort(80);
		config.setHost("my-perscon.appspot.com");

		cloudClient = new CloudClient(this.context, database, paths, config);
	}
	
	public void start()
	{		
		// TODO retry failed registration
		cloudClient.start();
	}
	
	public void stop()
	{
		if(this.cloudClient!=null)
		{
			this.cloudClient.requestStop();
		}
	}
	
	public void close()
	{
		if(this.database!=null)
		{
			this.database.close();
		}		
	}
	
	
	public void registerApplication(String applicationId, String applicationName, String applicationVersion) throws RegistrationException
	{
		horizon.perscon.model.Application a = new horizon.perscon.model.Application();
		a.setApplicationId(applicationId);
		a.setName(applicationName);
		a.setVersion(applicationVersion);
		
		try
		{
			horizon.perscon.model.Application [] as = ApplicationImpl.retrieveByApplicationId(database, applicationId);
			
			if(as!=null && as.length>0)
			{
				debug("application " + applicationId + " already registered");
				return;
			}
			
			ApplicationImpl.store(database, a);
			cloudClient.notifyEvent();
			
			// TODO notify local callbacks
		}
		catch(StorageException e)
		{
			throw e;
		}
	}
	
	// TODO
	public void addEventListener(IPersconServiceCallback callback, EventQuery template)
	{
		
	}

	// TODO
	public void removeEventListener(IPersconServiceCallback callback)
	{
		
	}
	
	public Event addEvent(String applicationId, Person person, Place place, Thing thing, PrivacyMask privacyMask) throws StorageException
	{
		Event event = new Event();
		
		event.setTimestamp(System.currentTimeMillis());
		event.setEventType(EventType.CREATED);
		event.setApplicationId(applicationId);
		
		if(person!=null)
			event.setPerson(person);
		
		if(place!=null)
			event.setPlace(place);
			
		if(thing!=null)
			event.setThing(thing);
		
		try
		{
			EventImpl.store(database, event);
			cloudClient.notifyEvent();
			
			// TODO notify local callbacks
		}
		catch(StorageException e)
		{
			throw e;
		}
		
		return event;
	}

	public Event [] matchEvents(EventQuery query) throws StorageException
	{
		Event [] events = null;
		
		try
		{
			events = EventImpl.match(database, query);
		}
		catch(StorageException e)
		{
			throw e;
		}
		
		return events;
	}
		
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database)
		{
			EventImpl.createTable(database);
			AttachmentImpl.createTable(database);
			PersonImpl.createTable(database);
			PlaceImpl.createTable(database);
			ThingImpl.createTable(database);
			
			ApplicationImpl.createTable(database);
			UploadDB.createTable(database);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
		{
			EventImpl.recreateTable(database);
			AttachmentImpl.recreateTable(database);
			PersonImpl.recreateTable(database);
			PlaceImpl.recreateTable(database);
			ThingImpl.recreateTable(database);

			ApplicationImpl.createTable(database);
			UploadDB.recreateTable(database);
		}
	}
}