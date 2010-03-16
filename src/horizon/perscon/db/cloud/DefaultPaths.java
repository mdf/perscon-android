
package horizon.perscon.db.cloud;

public class DefaultPaths extends Paths
{
	private static final String PATH_BASE = "/perscon/2";

	@Override
	public String getGetApplications()
	{
		// curl http://localhost:8080/perscon/2/$(cat user-id)/apps/

		return PATH_BASE+"/<user-id>/apps/";
	}

	@Override
	public String getGetUserDetails()
	{
		// curl http://localhost:8080/perscon/2/$(cat user-id)

		return PATH_BASE+"/<user-id>";
	}

	@Override
	public String getPutAttachment()
	{
		/*curl -d "mime=text/plain&body=UGVyc29uYWwgQ29udGFpbmVycyBGVFdcIQ==" \
    	http://localhost:8080/perscon/2/$(cat user-id)/$(cat dev-id)/atts/photo-thing-id/photo-id */
		
		return PATH_BASE+"/<user-id>/<device-id>/atts/<thing-id>/<attachment-id>";
	}

	@Override
	public String getPutEvent()
	{
		/* curl -d "aid=app-id&action=CREATED&ts=1268396629.654321&person=photographer-id&thing=photo-thing-id&place=photo-place-id" \
    	http://localhost:8080/perscon/2/$(cat user-id)/$(cat dev-id)/events/photo-event-id */

		return PATH_BASE+"/<user-id>/<device-id>/events/<event-id>";
	}

	@Override
	public String getPutPerson()
	{
		/*	curl -d "name=Foo%20Bar" \
	    http://localhost:8080/perscon/2/$(cat user-id)/$(cat dev-id)/people/photographer-id */
		
		return PATH_BASE+"/<user-id>/<device-id>/people/<person-id>";
	}

	@Override
	public String getPutPlace()
	{
		/*	curl -d "lat=1.0&lon=2.0&elev=5.0" \
	    http://localhost:8080/perscon/2/$(cat user-id)/$(cat dev-id)/places/photo-place-id */

		return PATH_BASE+"/<user-id>/<device-id>/places/<place-id>";
	}

	@Override
	public String getPutThing()
	{
		/* $ curl -d "origin=MyCamera" \
    	http://localhost:8080/perscon/2/$(cat user-id)/$(cat dev-id)/things/photo-thing-id */

		return PATH_BASE+"/<user-id>/<device-id>/things/<thing-id>";
	}

	@Override
	public String getRegisterApplication()
	{
		/*  curl -d "name=TestApp&version=0.1&callbacks=&cengine=" \
    	http://localhost:8080/perscon/2/$(cat user-id)/apps/app-id */

		return PATH_BASE+"/<user-id>/apps/<application-id>";
	}

	@Override
	public String getRegisterDevice()
	{
		/* curl -d "label=MyAndroidPhone&desc=A%20short%20description" \
	    http://localhost:8080/perscon/2/$(cat user-id)/devices >| dev-id */

		return PATH_BASE+"/<user-id>/devices";
	}

	@Override
	public String getRegisterUser()
	{
		// curl http://localhost:8080/perscon/2/

		return PATH_BASE+"/";
	}

	@Override
	public String getSetDeviceDetails()
	{
		/* curl -d "label=MyNewAndroidPhone&desc=Another%20short%20description" \
    	http://localhost:8080/perscon/2/$(cat user-id)/devices/$(cat dev-id) */

		return PATH_BASE+"/<user-id>/devices/<device-id>";
	}

	@Override
	public String getSetUserDetails()
	{
		// curl -d "name=Data%20Owner" http://localhost:8080/perscon/2/ >| user-id

		return PATH_BASE;
	}

}
