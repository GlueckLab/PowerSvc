package edu.cudenver.bios.powersvc.application;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;

import edu.cudenver.bios.powersvc.resource.DefaultResource;
import edu.cudenver.bios.powersvc.resource.PowerCurveResource;
import edu.cudenver.bios.powersvc.resource.PowerReportResource;
import edu.cudenver.bios.powersvc.resource.PowerResource;
import edu.cudenver.bios.powersvc.resource.SampleSizeReportResource;
import edu.cudenver.bios.powersvc.resource.SampleSizeResource;

public class PowerApplication extends Application
{   
    /**
     * @param parentContext
     */
    public PowerApplication(Context parentContext) throws Exception
    {
        super(parentContext);

        PowerLogger.getInstance().info("Statistical power service starting.");
    }

    @Override
    public Restlet createRoot() 
    {
        // Create a router Restlet that routes each call to a new instance of Resource.
        Router router = new Router(getContext());
        // Defines only one default route, self-identifies server
        router.attachDefault(DefaultResource.class);

        /* attributes of power resources */
        // Power calculation resource and report generating resource
        router.attach("/power", PowerResource.class);
        router.attach("/power/report", PowerReportResource.class);
        // Sample size resource
        router.attach("/samplesize", SampleSizeResource.class);
        router.attach("/samplesize/report", SampleSizeReportResource.class);
        // Power curve resource
        router.attach("/curve", PowerCurveResource.class);
                
        return router;
    }
}

