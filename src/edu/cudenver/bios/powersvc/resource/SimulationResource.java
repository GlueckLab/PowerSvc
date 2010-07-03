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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.powersvc.resource;

import java.io.IOException;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import edu.cudenver.bios.power.GLMMPowerCalculator;
import edu.cudenver.bios.power.Power;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.representation.ErrorXMLRepresentation;
import edu.cudenver.bios.powersvc.representation.GLMMPowerListXMLRepresentation;

/**
 * Resource for handling requests for power simulations.
 * See the PowerApplication class for URI mappings
 */
public class SimulationResource extends Resource
{
	private static final int DEFAULT_ITERATIONS = 10000;
	
	/**
	 * Create a new resource to handle power simulation requests.  Data
	 * is returned as XML.
	 * 
	 * @param context restlet context
	 * @param request http request object
	 * @param response http response object
	 */
    public SimulationResource(Context context, Request request, Response response) 
    {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
        
        
    }

    /**
     * Disallow GET requests
     */
    @Override
    public boolean allowGet()
    {
        return false;
    }

    /**
     * Disallow PUT requests
     */
    @Override
    public boolean allowPut()
    {
        return false;
    }

    /**
     * Allow POST requests to create a simulated power list
     */
    @Override
    public boolean allowPost() 
    {
        return  true;
    }

    /**
     * Process a POST request to perform a set of power
     * simulations.  Please see REST API documentation for details on
     * the entity body format.
     * 
     * @param entity HTTP entity body for the request
     */
    @Override 
    public void acceptRepresentation(Representation entity)
    {
        DomRepresentation rep = new DomRepresentation(entity);

        try
        {
            // parse the power parameters from the entity body
            GLMMPowerParameters params = ParameterResourceHelper.glmmPowerParametersFromDomNode(rep.getDocument().getDocumentElement());

            // create the appropriate power calculator for this model
            GLMMPowerCalculator calculator = new GLMMPowerCalculator();
            // get the simulated power results
            List<Power> results = calculator.getSimulatedPower(params, DEFAULT_ITERATIONS);
           
            // build the response xml
            GLMMPowerListXMLRepresentation response = new GLMMPowerListXMLRepresentation(results);
            getResponse().setEntity(response); 
            getResponse().setStatus(Status.SUCCESS_CREATED);
        }
        catch (IOException ioe)
        {
            PowerLogger.getInstance().error(ioe.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(ioe.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        catch (IllegalArgumentException iae)
        {
            PowerLogger.getInstance().error(iae.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(iae.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        catch (ResourceException re)
        {
            PowerLogger.getInstance().error(re.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(re.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(re.getStatus());
        }
    }

}
