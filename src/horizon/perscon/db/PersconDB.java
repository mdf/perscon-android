
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

import horizon.perscon.model.Attachment;
import horizon.perscon.model.Event;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
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
		
		config.setHttps(false);
		config.setPort(8080);
		config.setHost("192.168.0.149");
		
		//config.setHttps(true);
		//config.setPort(80);
		//config.setHost("1-test.latest.horizon-institute.appspot.com");
		
		cloudClient = new CloudClient(this.context, database, paths, config);
	}
	
	public void start()
	{		
		// TODO retry failed registration
		
        try
        {
    		cloudClient.startup();
        }
        catch(RegistrationException e)
        {
        	error("error registering cloud client, running locally only", e);
        	return;
        }

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
	
	public void runTests()
	{

		
		
		/*
		Thing t = new Thing();
		t.setFolder("folder");
		t.setMeta("some meta");
		t.setPermissions(1);
		
		Attachment a1 = new Attachment();
		a1.setData("a1 data");
		a1.setMeta("a1 meta");
		a1.setPermissions(1);
		
		Attachment a2 = new Attachment();
		a2.setData("a2 data");
		a2.setMeta("a2 meta");
		a2.setPermissions(2);
		
		t.setAttachments(new Attachment[] { a1, a2} );
		
		long l = ThingImpl.store(database, t);
		
		debug("l: " + t.getId());
		
		for(int i=0; i<t.getAttachments().length; i++)
		{
			debug("a" + i + ": " + t.getAttachments()[i].getId());
		}
		
		Place p = new Place();
		p.setElevation(12);
		p.setLatitude(51.123);
		p.setLongitude(-0.123);
		p.setMeta("place meta");
		p.setPermissions(2);

		long l4 = PlaceImpl.store(database, p);
		
		debug("p: " + l4);
		
		Person pe = new Person();
		pe.setPermissions(5);
		pe.setMeta("person meta");
		pe.setName("my name");
		
		long l5 = PersonImpl.store(database, pe);
		debug("pe: " + l5);

		*/
		
		debug("time: " + System.currentTimeMillis());

		Thing t = new Thing();
		t.setOrigin("test");
		//t.setMeta("some meta");
		t.setPermissions(1);
		
		Attachment a1 = new Attachment();
		a1.setBody("a1 data".getBytes());
		a1.setMimeType("text");
		//a1.setMeta("a1 meta");
		a1.setPermissions(1);
		

		/*
		long l69 = AttachmentImpl.store(database, a1);
		Attachment a6 = AttachmentImpl.retrieveUnique(database, l69);
		String s1 = new String(a6.getBody());
		debug("s: " + s1);
		*/
		
		Attachment a2 = new Attachment();
		a2.setBody("a2 data".getBytes());
		a2.setMimeType("text");
		//a2.setMeta("a2 meta");
		a2.setPermissions(2);
		
		t.setAttachments(new Attachment[] { a1, a2} );

	
		Place p = new Place();
		p.setElevation(12);
		p.setLatitude(51.123);
		p.setLongitude(-0.123);
		//p.setMeta("place meta");
		p.setPermissions(2);

		Person pe = new Person();
		pe.setPermissions(5);
		//pe.setMeta("person meta");
		pe.setName("my name");
		
		/*

		Event e = new Event();
		e.setEventType(Event.EventType.CREATED);
		e.setTimestamp(System.currentTimeMillis());
		
		debug("ts: " + e.getTimestamp());
		
		e.setPermissions(59);
		e.setApplicationId("app-2");
		
		e.setThing(t);
		e.setPerson(pe);
		e.setPlace(p);
		
		long l6 = EventImpl.store(database, e);
		
		debug("l6: " + l6);
		Event e2 = EventImpl.retrieveUnique(database, l6);
		debug("ts2: " + e2.getTimestamp());


		Event e3 = new Event();
		e3.setEventType(Event.EventType.CREATED);
		e3.setTimestamp(System.currentTimeMillis());
		e3.setThing(t);
		long l7 = EventImpl.store(database, e3);
		Event e4 = EventImpl.retrieveUnique(database, l7);
		*/
		/*
		cc.syncThing(t);
		cc.syncPlace(p);
		cc.syncPerson(pe);
		cc.syncAttachment(a1);
		cc.syncAttachment(a2);
		cc.syncEvent(e);
		
		*/		

		
		
		/*
		Attachment a1 = new Attachment();
		a1.setData("a1 data");
		a1.setMeta("a1 meta");
		a1.setPermissions(1);
		a1.setThingId(123l);
		
		long l69 = AttachmentImpl.store(database, a1);
		
		
		Attachment a6 = AttachmentImpl.retrieveUnique(database, l69);
		*/
		
		/*

		EventQuery eq = new EventQuery();
		eq.addConstraintNotNull("person");
		eq.addConstraintNe("thing", new Long(1));
		eq.addConstraintEq("eventType", EventType.CREATED.toString());
		
		EventImpl.match(database, eq);

*/
		
		this.addEvent("app-2", pe, p, t);
		
		try
		{
			Thread.sleep(5000);
		}
		catch(Exception e)
		{
			
		}

		this.addEvent("app-2", pe, p, t);
		
		debug("done tests");
		
		/*
		UploadDB [] ups = UploadDB.getToUpload(database);
		
		for(int i=0; i<ups.length; i++)
		{
			debug("ups: " + i + " " + ups[i].getId() + " " + ups[i].getType() + " " + ups[i].getUploaded());
			UploadDB.notifyUploaded(database, ups[i]);
		}
		
		UploadDB [] ups2 = UploadDB.getToUpload(database);
		
		debug("done ups");
		
		for(int i=0; i<ups2.length; i++)
		{
			debug("ups2: " + i + " " + ups2[i].getId() + " " + ups2[i].getType() + " " + ups2[i].getUploaded());
			UploadDB.notifyUploaded(database, ups2[i]);
		}
		debug("done ups2"); */
		
	}

	public void registerApplication(String applicationId, String applicationName, String applicationVersion) throws RegistrationException
	{
		if(cloudClient!=null)
		{
			try
			{
				cloudClient.registerApp(applicationId, applicationName, applicationVersion);
			}
			catch(RegistrationException e)
			{
				throw e;
			}
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
	
	public Event addEvent(String applicationId, Person person, Place place, Thing thing) throws StorageException
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
			
			UploadDB.recreateTable(database);
		}
	}
}