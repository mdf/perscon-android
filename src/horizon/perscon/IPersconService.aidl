
package horizon.perscon;

import horizon.perscon.IPersconServiceCallback;

import horizon.perscon.model.Event;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.Thing;

import horizon.perscon.model.EventQuery;

interface IPersconService
{
	void registerApplication(in String applicationId, in String applicationName, in String applicationVersion);

	void addEventListener(IPersconServiceCallback callback, in EventQuery template);
	
	void removeEventListener(IPersconServiceCallback callback);
		
	Event [] match(in EventQuery template);
	
	Event add(in String applicationId, in Person person, in Place place, in Thing thing);
}