package horizon.perscon.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import horizon.perscon.IPersconService;
import horizon.perscon.IPersconServiceCallback;

import horizon.perscon.R;

import horizon.perscon.db.PersconDB;
import horizon.perscon.model.Attachment;
import horizon.perscon.model.Event;
import horizon.perscon.model.EventQuery;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.PrivacyMask;
import horizon.perscon.model.Thing;
import horizon.perscon.model.Event.EventType;

import static horizon.perscon.Constants.*;

public class TestActivity extends Activity
{
	PersconDB pc = null;

    IPersconService myService = null;
    
    // TESTING
    String appId = "e908b46e9d003ae4a857a54b38acafc57f78ab49";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);
        
        Button button;
        
        button = (Button)findViewById(R.id.registerbutton);
        button.setOnClickListener(registerButtonListener);
        
        button = (Button)findViewById(R.id.testsbutton);
        button.setOnClickListener(testButtonListener);
        
        bindService(new Intent(IPersconService.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);

    }
    
    private OnClickListener testButtonListener = new OnClickListener()
    {
		public void onClick(View arg0)
		{
			runTests();
		}
    };
    
    private OnClickListener registerButtonListener = new OnClickListener()
    {
		public void onClick(View arg0)
		{	
			doRegister();
		}
    };
    
    protected void runTests()
    {
		debug("running tests");		
		
		try
		{
			this.myService.registerApplication(appId, "appname-1", "0.1");
		}
		catch(RemoteException e)
		{
			error("registerApplication", e);
		}

		Attachment a1 = new Attachment();
		a1.setBody("a1 data".getBytes());
		a1.setMimeType("text");
		a1.setPermissions(1);
		
		Attachment a2 = new Attachment();
		a2.setBody("a2 data".getBytes());
		a2.setMimeType("text");
		a2.setPermissions(2);
		
		Thing thing = new Thing();
		thing.setOrigin("test");
		thing.setPermissions(1);
		thing.setAttachments(new Attachment[] { a1, a2} );
		
		Place place = new Place();
		place.setElevation(12);
		place.setLatitude(51.123);
		place.setLongitude(-0.123);
		place.setPermissions(2);

		Person person = new Person();
		person.setPermissions(5);
		person.setName("my name");
		
		PrivacyMask pm = new PrivacyMask();
		pm.setField(PrivacyMask.PRIV_PLACE, true);
		pm.setAllPrivate();
		pm.setAllPublic();
		
		try
		{
			Event e = this.myService.add(appId, person, place, thing, pm);
			debug("eventid: " + e.getId());
		}
		catch(RemoteException e)
		{
			error("addEvent", e);
		}
		
		EventQuery eq = new EventQuery();
		eq.addConstraintNotNull("person");
		eq.addConstraintNe("thing", new Long(100));
		eq.addConstraintEq("eventType", EventType.CREATED.toString());
		
		try
		{
			Event [] es = this.myService.match(eq);
			
			if(es!=null)
			{
				for(int i=0;i<es.length;i++)
				{
					debug("match event id: " + es[i].getId());
				}
			}
		}
		catch(RemoteException e)
		{
			error("query", e);
		}
    }
    
    protected void doRegister()
    {
		debug("do register test");
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    }
    
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(serviceConnection);
	}
	
    private IPersconServiceCallback myCallback = new IPersconServiceCallback.Stub()
	{
		
		public void eventAdded(Event event) throws RemoteException
		{
			// TODO Auto-generated method stub
			debug("eventAdded");
		}
	};
	
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
        	debug("connected");

        	myService = IPersconService.Stub.asInterface(service);

            try
            {
        		myService.addEventListener(myCallback, null);
            }
            catch(RemoteException e)
            {
            	error("addEventListener", e);
            }

            Toast.makeText(TestActivity.this, R.string.perscon_connected, Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className)
        {
        	debug("service disconnected");
            
        	myService = null;

            Toast.makeText(TestActivity.this, R.string.perscon_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

}