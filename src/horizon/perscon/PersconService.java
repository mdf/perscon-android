
package horizon.perscon;

import horizon.perscon.db.PersconDB;

import horizon.perscon.model.Event;
import horizon.perscon.model.EventQuery;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.PrivacyMask;
import horizon.perscon.model.Thing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;

import static horizon.perscon.Constants.*;

public class PersconService extends Service
{
    private NotificationManager notificationManager;
    
    private PersconDB perscon;

    final RemoteCallbackList<IPersconServiceCallback> serviceCallbacks = new RemoteCallbackList<IPersconServiceCallback>();
    
    @Override
    public void onCreate()
    {
        debug("service oncreate");
        
        perscon = new PersconDB(this);
        perscon.start();
        
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        showNotification();
    }

    @Override
    public void onDestroy()
    {    
        debug("service ondestroy");
        
        serviceCallbacks.kill();
        
        perscon.stop();
        
    	notificationManager.cancel(R.string.perscon_started);

    	Toast.makeText(this, R.string.perscon_stopped, Toast.LENGTH_SHORT).show();
    }
    

    @Override
    public IBinder onBind(Intent intent)
    {
    	return serviceBinder;
    }
    
    private final IPersconService.Stub serviceBinder = new IPersconService.Stub()
    {		
		public void addEventListener(IPersconServiceCallback callback, EventQuery template) throws RemoteException
		{
			// TODO callbacks
			if (callback != null)
	        {
	        	debug("add event listener");
	        	serviceCallbacks.register(callback);
	        	perscon.addEventListener(callback, template);
	        }
		}    	
    	
		public void removeEventListener(IPersconServiceCallback callback) throws RemoteException
		{
			// TODO callbacks
            if (callback != null)
            {
            	debug("remove event listener");
            	serviceCallbacks.unregister(callback);
	        	perscon.removeEventListener(callback);
            }			
		}
		
		public Event add(String applicationId, Person person, Place place, Thing thing, PrivacyMask privacyMask) throws RemoteException
		{
			// TODO errors
			try
			{
				return perscon.addEvent(applicationId, person, place, thing, privacyMask);				
			}
			catch(Exception e)
			{
				error("eventAdd error", e);
			} 
			
			throw new RemoteException();
		}

		public Event[] match(EventQuery template) throws RemoteException
		{
			// TODO errors
			try
			{
				return perscon.matchEvents(template);
			}
			catch(Exception e)
			{
				error("match error", e);				
			}
			throw new RemoteException();
		}

		public void registerApplication(String applicationId, String applicationName, String applicationVersion) throws RemoteException
		{
			try
			{
				perscon.registerApplication(applicationId, applicationName, applicationVersion);
			}
			catch(Exception e)
			{
				error("registerApplication error", e);
				throw new RemoteException();
				// ?
			}
		}
	};
	
	public void notifyCallbacks()
	{
		// TODO callbacks
		debug("notifyCallbacks");
		
        final int n = serviceCallbacks.beginBroadcast();
        
        for (int i=0; i<n; i++)
        {
        	try
        	{
            	serviceCallbacks.getBroadcastItem(i).eventAdded(null);
            }
        	catch (RemoteException e)
            {
        		error("callbacks", e);
            }
        }
        
        serviceCallbacks.finishBroadcast();
        
	}
    
    private void showNotification()
    {
    	CharSequence text = getText(R.string.perscon_started);

    	Notification notification = new Notification(R.drawable.stat_sample, text, System.currentTimeMillis());

    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ConfigActivity.class), 0);

    	notification.setLatestEventInfo(this, getText(R.string.perscon_label), text, contentIntent);

    	notificationManager.notify(R.string.perscon_started, notification);
    }

}
