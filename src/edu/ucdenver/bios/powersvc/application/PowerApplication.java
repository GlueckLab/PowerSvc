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
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Router;
import org.restlet.service.StatusService;

import edu.ucdenver.bios.powersvc.resource.DefaultResource;
import edu.ucdenver.bios.powersvc.resource.DetectableDifferenceServerResource;
import edu.ucdenver.bios.powersvc.resource.PowerMatrixHTMLServerResource;
import edu.ucdenver.bios.powersvc.resource.PowerMatrixServerResource;
import edu.ucdenver.bios.powersvc.resource.PowerServerResource;
import edu.ucdenver.bios.powersvc.resource.SampleSizeServerResource;
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
        setStatusService(new PowerStatusService());
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
        router.attach("/samplesize", SampleSizeServerResource.class);
        router.attach("/difference", DetectableDifferenceServerResource.class);
        router.attach("/matrix", PowerMatrixServerResource.class);
        router.attach("/matrix/html", PowerMatrixHTMLServerResource.class);
        // unit test resource - easier to collaborate with remote testers
        //this way
        router.attach("/testf", FTestResource.class);

        return router;
    }

    /**
     * A StatusService subclass, to override the error representation.
     */
    private static final class PowerStatusService extends StatusService {
        private static final String MAILTO_URL = "mailto:samplesizeshop@gmail.com?subject=GLIMMPSE%20issue";

        @Override
        public Representation getRepresentation(Status status, Request request, Response response) {
            if (! Status.CLIENT_ERROR_BAD_REQUEST.equals(status)) {
                return super.getRepresentation(status, request, response);
            }

            final StringBuilder sb = new StringBuilder();

            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>Bad Request</title>\n");
            sb.append("</head>\n");
            sb.append("<body style=\"font-family: sans-serif;\">\n");

            sb.append("<p>");
            sb.append("We're sorry, we are unable to process your request.");
            sb.append("</p>\n");

            sb.append("<p>");
            sb.append("If you need help interpreting the message below, please contact <a href=\"");
            sb.append(obfuscation(MAILTO_URL));
            sb.append("\">technical support</a>.");
            sb.append("</p>\n");

            sb.append("<div style=\"margin-left: 20px\">");
            sb.append(status.getDescription() != null ? status.getDescription() : "Unknown error");
            sb.append("</div>\n");

            sb.append("</body>\n");
            sb.append("</html>\n");

            return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
        }

        /**
         * Return an obfuscation of a string, suitable for use as an anchor element href attribute
         * in HTML.
         *
         * @param s The string.
         *
         * @return An obfuscation of the string.
         */
        private static final String obfuscation(String s) {
            StringBuilder sb = new StringBuilder(500);

            char[] ca = s.toCharArray();
            for (int i = 0, n = ca.length; i < n; ++ i) {
                sb.append("&#" + (int) ca[i] + ";");
            }

            return sb.toString();
        }
    }
}
