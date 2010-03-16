
package horizon.perscon.db.cloud;

public class NotifyLock
{
	Boolean notification = false;
	
	public synchronized void notifyEvent()
	{
		notification = true;
        notifyAll();
    }
	
    public synchronized void waitForNotification()
    {    
    	try
    	{
    		while(notification == false)
    		{
    			wait();
    		}
    	}
    	catch (InterruptedException ie)
    	{
        	notification = false;
    		return;
    	}
    	
    	notification = false;
    	return;
    }
}
