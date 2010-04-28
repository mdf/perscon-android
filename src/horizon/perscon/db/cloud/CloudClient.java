
package horizon.perscon.db.cloud;

import horizon.perscon.db.ApplicationImpl;
import horizon.perscon.db.AttachmentImpl;
import horizon.perscon.db.EventImpl;
import horizon.perscon.db.PersonImpl;
import horizon.perscon.db.PlaceImpl;
import horizon.perscon.db.ThingImpl;
import horizon.perscon.db.UploadDB;
import horizon.perscon.db.cloud.model.Application;
import horizon.perscon.db.cloud.model.Device;
import horizon.perscon.db.cloud.model.User;
import horizon.perscon.model.Attachment;
import horizon.perscon.model.Event;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.Thing;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;

import static horizon.perscon.Constants.*;

public class CloudClient extends Thread implements Runnable
{	
	public static final String SERVICE_NAME = "perscon-gae";
	public static final String PREF_USERID = "userId";
	public static final String PREF_DEVICEID = "deviceId";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private SQLiteDatabase database;

	private Context context;
	
	private Paths paths = null;	
	private Configuration config = null;
	
	private HttpClient httpClient;
	private HttpHost httpHost;
	
	private User user = null;
	private Device device = null;
	
	private boolean running = false;
	
	private NotifyLock lock = null;
	
	public CloudClient(Context context, SQLiteDatabase database, Paths paths, Configuration config)
	{
		this.database = database;
		this.context = context;
		this.paths = paths;
		this.config = config;
		
		httpClient = new DefaultHttpClient();
		httpHost = new HttpHost(
				this.config.getHost(),
				this.config.getPort(),
				this.config.isHttps() ? "https" : "http");
		httpClient.getParams().setParameter("http.default-host", httpHost);
		
		
		this.lock = new NotifyLock();
	}
		
	public void startup() throws RegistrationException
	{
		// TODO move initial ids into parent, 404s
		try
		{
			SharedPreferences settings = context.getSharedPreferences(SERVICE_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			
			String userId = settings.getString(PREF_USERID, null);
			//String userId = "fbf6df379eb038f5e95477f9d1a86edae8872bdf";
			//String userId = null;
			
	 		if(userId==null)
			{
				debug("no userId found, doing install");
				user = this.registerUser();
	
				editor.putString(PREF_USERID, user.getUid());
				
				// TODO set user details
				
			}
			else
			{
				debug("found local userId: " + userId);
				user = this.getUserDetails(userId);
			}

	 		debug("userid:" + user.getUid());
	
			String deviceId = settings.getString(PREF_DEVICEID, null);
			//String deviceId = "93d3093b25778ab9c42d05f74d558e8093fbb0e5";
			//String deviceId = null;
			
			if(deviceId==null)
			{
				debug("no deviceId found, doing install");
	
				TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
				String imei = telephonyManager.getDeviceId();
				
				device = this.registerDevice(imei, "my phone");
				
				editor.putString(PREF_DEVICEID, device.getDeviceId());
			}
			else
			{
				// TODO get devices from cloud

				debug("found local deviceId: " + deviceId);
				device = new Device();
				device.setDeviceId(deviceId);
			}
			
			debug("deviceid: " + device.getDeviceId());
	
			editor.commit();
		}
		catch(RegistrationException e)
		{
			throw e;
		}
	}
	
	public void notifyEvent()
	{
		debug("cloud client notify");
		
		lock.notifyEvent();
	}
	
	public void run()
	{
        try
        {
    		this.startup();
        }
        catch(RegistrationException e)
        {
        	error("error registering cloud client, running locally only", e);
        	return;
        }
		
		debug("cloud client running");
		
		this.running = true;
		
		while(running)
		{
			lock.waitForNotification();
		
			UploadDB [] ups = UploadDB.getToUpload(database);
			
			// do applications first

			for(int i=0; i<ups.length; i++)
			{
				try
				{
					if(ApplicationImpl.getTableName().equals(ups[i].getType()))
					{
						debug("upload app: " + i + " " + ups[i].getId() + " " + ups[i].getType() + " " + ups[i].getUploaded());
						horizon.perscon.model.Application application = ApplicationImpl.retrieveUnique(database, ups[i].getId());
						this.registerApplication(application);
					}
					
					UploadDB.notifyUploaded(database, ups[i]);
				}
				catch(RegistrationException e)
				{
					error("app reg exception", e);
				}
				catch(SyncException e)
				{
					error("app sync exception", e);					
				}
			}
			
			for(int i=0; i<ups.length; i++)
			{
				// TODO only upload event if member uploads successful
				
				debug("upload: " + i + " " + ups[i].getId() + " " + ups[i].getType() + " " + ups[i].getUploaded());
				
				try
				{
					if(EventImpl.getTableName().equals(ups[i].getType()))
					{
						Event event = EventImpl.retrieveUnique(database, ups[i].getId());
						this.syncEvent(event);
					}
					else if(PersonImpl.getTableName().equals(ups[i].getType()))
					{
						Person person = PersonImpl.retrieveUnique(database, ups[i].getId());
						this.syncPerson(person);
					}
					else if(PlaceImpl.getTableName().equals(ups[i].getType()))
					{
						Place place = PlaceImpl.retrieveUnique(database, ups[i].getId());
						this.syncPlace(place);
					}
					else if(ThingImpl.getTableName().equals(ups[i].getType()))
					{
						Thing thing = ThingImpl.retrieveUnique(database, ups[i].getId());
						this.syncThing(thing);
					}
					else if(AttachmentImpl.getTableName().equals(ups[i].getType()))
					{
						Attachment attachment = AttachmentImpl.retrieveUnique(database, ups[i].getId());
						this.syncAttachment(attachment);
					}
					
					UploadDB.notifyUploaded(database, ups[i]);
				}
				catch(SyncException e)
				{
					// FIXME
					error("upload error", e);
				}
			}
			
			debug("done uploads");
		}
	}
	
	public void requestStop()
	{
		this.running = false;
	}
	
	
	protected User registerUser()
	{
		debug("trying to registerUser");
		
		HttpGet get = new HttpGet();
		HttpResponse response = null;

		String path = paths.getRegisterUser();

		try
		{
			get.setURI(new URI(path));
			response = httpClient.execute(get);
		}
		catch(Exception e)
		{
			error("Unable to register user", e);
			throw new RegistrationException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to register user, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new RegistrationException("Unable to register user, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}
		
		User user = null;
		
		try
		{   
			String userString = EntityUtils.toString(response.getEntity());
			user = mapper.readValue(userString, User.class);
		}
		catch(IOException e)
		{
			error("Unable to parse user json response", e);
			throw new RegistrationException(e);			
		}
		
		return user;
	}
	
	protected User getUserDetails(String userId)
	{
		debug("trying to getUserDetails");
		
		HttpGet get = new HttpGet();
		HttpResponse response = null;

		String path = paths.getGetUserDetails()
						.replace("<user-id>", userId);

		try
		{
			get.setURI(new URI(path));
			response = httpClient.execute(get);
		}
		catch(Exception e)
		{
			error("Unable to get user details", e);
			throw new RegistrationException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to get user details, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new RegistrationException("Unable to get user details, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}
		
		User user = null;
		
		try
		{   
			String userString = EntityUtils.toString(response.getEntity());
			user = mapper.readValue(userString, User.class);
		}
		catch(IOException e)
		{
			error("Unable to parse userDetails json response", e);
			throw new RegistrationException(e);			
		}
		
		return user;
	}
	
	protected Device registerDevice(String label, String description)
	{
		debug("trying to registerDevice");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getRegisterDevice()
						.replace("<user-id>", this.user.getUid());

		try
		{
			post.setURI(new URI(path));

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("label", label));
			pairs.add(new BasicNameValuePair("desc", description));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
		}
		catch(Exception e)
		{
			error("Unable to register device", e);
			throw new RegistrationException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to register device, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new RegistrationException("Unable to register device, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}
		
		String deviceId = null;
		
		try
		{   
			deviceId = EntityUtils.toString(response.getEntity());
		}
		catch(IOException e)
		{
			error("Unable to parse device response", e);
			throw new RegistrationException(e);			
		}
		
		Device d = new Device();
		d.setDeviceId(deviceId);
		d.setDesc(description);
		d.setLabel(label);
		
		return d;
	}
	
	protected void registerApplication(horizon.perscon.model.Application application)
	{
		debug("trying to registerApplication");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getRegisterApplication()
			.replace("<user-id>", user.getUid())
			.replace("<application-id>", application.getApplicationId());

		try
		{
			post.setURI(new URI(path));

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("name", application.getName()));
			pairs.add(new BasicNameValuePair("version", application.getVersion()));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
		}
		catch(Exception e)
		{
			error("Unable to register application", e);
			throw new RegistrationException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to register application, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new RegistrationException("Unable to register application, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}

		try
		{
			String responseString = EntityUtils.toString(response.getEntity());
			debug("app reg response: " + responseString);
		}
		catch(IOException e)
		{
			error("Unable to parse app reg response", e);
			throw new RegistrationException(e);			
		}
	}
	
	protected Application[] getApplications()
	{
		debug("trying to getApplications");
		
		HttpGet get = new HttpGet();
		HttpResponse response = null;

		String path = paths.getGetApplications()
						.replace("<user-id>", user.getUid());

		try
		{
			get.setURI(new URI(path));
			response = httpClient.execute(get);
		}
		catch(Exception e)
		{
			error("Unable to get user applications", e);
			throw new RegistrationException(e);
		}

		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to get user applications, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new RegistrationException("Unable to get user applications, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}

		try
		{   
			String responseString = EntityUtils.toString(response.getEntity());
			ArrayList<Application> apps = mapper.readValue(responseString, new TypeReference<ArrayList<Application>>() { });
			return apps.toArray(new Application[apps.size()]);
		}
		catch(IOException e)
		{
			error("Unable to parse user applications json response", e);
			throw new RegistrationException(e);			
		}
	}
	
	protected void syncPlace(Place place)
	{
		debug("trying to syncPlace");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getPutPlace()
			.replace("<user-id>", user.getUid())
			.replace("<device-id>", device.getDeviceId())
			.replace("<place-id>", place.getId().toString());

		debug(path);
		
		try
		{
			post.setURI(new URI(path));
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();			
			pairs.add(new BasicNameValuePair("lat", place.getLatitude().toString()));
			pairs.add(new BasicNameValuePair("lon", place.getLongitude().toString()));
			
			if(place.getElevation()!=null)			
				pairs.add(new BasicNameValuePair("elev", place.getElevation().toString()));
			
			if(place.getMeta()!=null)
				pairs.add(new BasicNameValuePair("meta", place.getElevation().toString()));
			
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
					
		}
		catch(Exception e)
		{
			error("Unable to sync place", e);
			throw new SyncException(e);
		}
				  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to sync place, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new SyncException("Unable to sync person, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}	
	}
	
	protected void syncPerson(Person person)
	{
		debug("trying to syncPerson");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getPutPerson()
			.replace("<user-id>", user.getUid())
			.replace("<device-id>", device.getDeviceId())
			.replace("<person-id>", person.getId().toString());

		debug(path);
		
		try
		{
			post.setURI(new URI(path));
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();			
			pairs.add(new BasicNameValuePair("name", person.getName()));
			
			if(person.getMeta()!=null)	
				pairs.add(new BasicNameValuePair("meta", person.getName()));
			
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
		}
		catch(Exception e)
		{
			error("Unable to sync person", e);
			throw new SyncException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to sync person, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new SyncException("Unable to sync person, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}	
	}
	
	@SuppressWarnings("unchecked")
	protected void syncThing(Thing thing)
	{
		debug("trying to syncThing");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getPutThing()
			.replace("<user-id>", user.getUid())
			.replace("<device-id>", device.getDeviceId())
			.replace("<thing-id>", thing.getId().toString());
		
		debug(path);

		try
		{
			post.setURI(new URI(path));
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();			
			pairs.add(new BasicNameValuePair("origin", thing.getOrigin()));
			
			if(thing.getMeta()!=null)
			{
				// HACK to get around parcelable size limit
				try
				{
					Map<String,Object> metaData = mapper.readValue(thing.getMeta(), Map.class);
					
					if(metaData.containsKey("filename"))
					{
						String filename = (String)metaData.get("filename");
						String base64file = Base64.encodeFromFile(filename);		
						metaData.put("body", base64file);
						
						thing.setMeta(mapper.writeValueAsString(metaData));
					}					
				}
				catch(Exception err)
				{
					error("error parsing meta json, dumping", err);
				}
				pairs.add(new BasicNameValuePair("meta", thing.getMeta()));
			}		
					
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
		}
		catch(Exception e)
		{
			error("Unable to sync thing", e);
			throw new SyncException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to sync thing, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new SyncException("Unable to sync thing, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}
	}
	
	protected void syncAttachment(Attachment attachment)
	{
		debug("trying to syncAttachment");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getPutAttachment()
			.replace("<user-id>", user.getUid())
			.replace("<device-id>", device.getDeviceId())
			.replace("<thing-id>", attachment.getThingId().toString())
			.replace("<attachment-id>", attachment.getId().toString());

		try
		{
			post.setURI(new URI(path));
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();			
			pairs.add(new BasicNameValuePair("mime", attachment.getMimeType()));
			pairs.add(new BasicNameValuePair("body", Base64.encodeBytes(attachment.getBody())));
			
			if(attachment.getMeta()!=null)	
				pairs.add(new BasicNameValuePair("meta", attachment.getMeta()));
					
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
		}
		catch(Exception e)
		{
			error("Unable to sync attachment", e);
			throw new SyncException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to sync attachment, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new SyncException("Unable to sync attachment, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}
	}
	
	protected void syncEvent(Event event)
	{
		debug("trying to syncEvent");
		
		HttpPost post = new HttpPost();
		HttpResponse response = null;

		String path = paths.getPutEvent()
			.replace("<user-id>", user.getUid())
			.replace("<device-id>", device.getDeviceId())
			.replace("<event-id>", event.getId().toString());

		debug(path);
		
		try
		{
			post.setURI(new URI(path));
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();			
			pairs.add(new BasicNameValuePair("aid", event.getApplicationId()));
			pairs.add(new BasicNameValuePair("ts", ""+(((float)event.getTimestamp())/1000.0f)));
			
			//pairs.add(new BasicNameValuePair("action", event.getEventType().toString()));
			
			if(event.getPerson()!=null)	
				pairs.add(new BasicNameValuePair("person", event.getPerson().getId().toString()));			

			if(event.getPlace()!=null)	
				pairs.add(new BasicNameValuePair("place", event.getPlace().getId().toString()));

			if(event.getThing()!=null)	
				pairs.add(new BasicNameValuePair("thing", event.getThing().getId().toString()));
			
			if(event.getMeta()!=null)	
				pairs.add(new BasicNameValuePair("meta", event.getMeta()));
			
			if(event.getPrivacyMask()!=null)
				pairs.add(new BasicNameValuePair("mask", Arrays.toString(event.getPrivacyMask().getMask())));
			
			/*
			for(int i=0;i<pairs.size();i++)
			{
				debug("ev: " + pairs.get(i).getName() + " " + pairs.get(i).getValue().toString());
			}
			*/
			
			post.setEntity(new UrlEncodedFormEntity(pairs));
			
			response = httpClient.execute(post);
		}
		catch(Exception e)
		{
			error("Unable to sync event", e);
			throw new SyncException(e);
		}
		  
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		{
			debug("Unable to sync event, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
			throw new SyncException("Unable to sync event, server said "
					+ response.getStatusLine().getReasonPhrase()
					+ response.getStatusLine().getStatusCode());
		}
	}
}
