
package horizon.perscon;

import horizon.perscon.model.Event;

oneway interface IPersconServiceCallback
{
	void eventAdded(in Event event);
}