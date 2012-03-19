/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 *
 * Copyright (C) 2010 Regents of the University of Colorado.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.powersvc.application;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import edu.ucdenver.bios.powersvc.resource.DefaultResource;
import edu.ucdenver.bios.powersvc.resource.PowerServerResource;
import edu.ucdenver.bios.powersvc.resource.test.FTestResource;

/**
 * Main Restlet application class for the Power Service.
 * Defines URI mappings to the appropriate power,
 * sample size, or detectable difference resource
 *
 * @author Sarah Kreidler
 */
public class PowerApplication extends Application {

    /**
     * Class which dispatches http requests to the appropriate
     * handler class for the power service.
     *
     * @param parentContext servlet context
     * @throws Exception Thrown when Restlet fails to create an instance
     * of the power application.
     */
    public PowerApplication(final Context parentContext)
    throws Exception {
        super(parentContext);

        PowerLogger.getInstance().info("Statistical power service starting.");
    }

    /**
     * Define URI mappings for incoming power, sample size,
     * and detectable difference requests.
     * @return Restlet object
     */
    @Override
    public final Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance
        // of Resource.
        Router router = new Router(getContext());
        // Defines only one default route, self-identifies server
        router.attachDefault(DefaultResource.class);

        /* attributes of power resources */
        // Power, sample size, and detectable difference  calculation resource
        router.attach("/power", PowerServerResource.class);

        // unit test resource - easier to collaborate with remote testers
        //this way
        router.attach("/testf", FTestResource.class);

        return router;
    }
}

