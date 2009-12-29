/**
 * Default request resource.  Called from the URI /power
 * Simply returns a self-identifying message for the server
 */

package edu.cudenver.bios.powersvc.resource;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

public class DefaultResource extends Resource
{
    public DefaultResource(Context context, Request request, Response response) 
    {
        super(context, request, response);

        // This representation has only one type of representation.
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    /**
     * Returns a full representation for a given variant.
     */
    @Override
    public Representation represent(Variant variant) {
        Representation representation = 
            new StringRepresentation("Statistical Power REST Service", MediaType.TEXT_PLAIN);

        return representation;
    }
}
