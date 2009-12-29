package edu.cudenver.bios.powersvc.application;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;

import edu.cudenver.bios.powersvc.resource.DefaultResource;
import edu.cudenver.bios.powersvc.resource.PowerResource;
import edu.cudenver.bios.powersvc.resource.SampleSizeResource;

public class PowerApplication extends Application
{   
    /**
     * @param parentContext
     */
    public PowerApplication(Context parentContext) throws Exception
    {
        super(parentContext);

        PowerLogger.getInstance().info("data feed service starting.");
    }

    @Override
    public Restlet createRoot() 
    {
        // Create a router Restlet that routes each call to a new instance of Resource.
        Router router = new Router(getContext());
        // Defines only one default route, self-identifies server
        router.attachDefault(DefaultResource.class);

        /* just self-identify server if no test specified */
        router.attach("/power", DefaultResource.class);

        /* attributes of power resources */
        // Power calculation resource
        router.attach("/power/model/{modelName}", PowerResource.class);
        // Sample size resource
        router.attach("/samplesize/model/{modelName}", SampleSizeResource.class);
        
        return router;
    }
}

