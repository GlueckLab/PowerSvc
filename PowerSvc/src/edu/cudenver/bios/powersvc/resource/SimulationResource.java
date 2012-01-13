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

import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.cudenver.bios.power.GLMMPowerCalculator;
import edu.cudenver.bios.power.Power;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.representation.GLMMPowerListXMLRepresentation;

/**
 * Resource for handling requests for power simulations.
 * See the PowerApplication class for URI mappings
 * 
 * @author Sarah Kreidler
 */
public class SimulationResource extends ServerResource
{
	private static final int MAX_ITERATIONS = 1000000;
	private static final int DEFAULT_ITERATIONS = 10000;
	private int iterations = DEFAULT_ITERATIONS;
	
	/**
	 * Create a new resource to handle power simulation requests.  Data
	 * is returned as XML.  This resource takes a single query parameter
	 * "iterations" which specifies the number of simulation iterations to
	 * run.  If the value is less than 0 or greater than 1 million, it will be
	 * reset to the default number of iterations.
	 *  
	 * @param context restlet context
	 * @param request http request object
	 * @param response http response object
	 */
    public SimulationResource() 
    {
        super();
        
        String iterationParam = getQuery().getFirstValue(PowerConstants.REQUEST_ITERATIONS);
        if (iterationParam != null && !iterationParam.isEmpty())
        {
        	iterations = Integer.parseInt(iterationParam);
        	if (iterations < 0 || iterations > MAX_ITERATIONS) 
        	{
        		iterations = DEFAULT_ITERATIONS;
        	}
        }
    }

    /**
     * Process a POST request to perform a set of power
     * simulations.  Please see REST API documentation for details on
     * the entity body format.
     * 
     * @param entity HTTP entity body for the request
     */
    @Override 
    public GLMMPowerListXMLRepresentation post(Representation entity)
    throws ResourceException
    {
        try
        {
        	DomRepresentation rep = new DomRepresentation(entity);
            // parse the power parameters from the entity body
            GLMMPowerParameters params = ParameterResourceHelper.glmmPowerParametersFromDomNode(rep.getDocument().getDocumentElement());

            // create the appropriate power calculator for this model
            GLMMPowerCalculator calculator = new GLMMPowerCalculator();
            // get the simulated power results
            List<Power> results = calculator.getSimulatedPower(params, iterations);
           
            // build the response xml
            GLMMPowerListXMLRepresentation response = new GLMMPowerListXMLRepresentation(results);
            return response;
        }
        catch (IOException ioe)
        {
            PowerLogger.getInstance().error(ioe.getMessage());
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ioe.getMessage());
        }
        catch (IllegalArgumentException iae)
        {
            PowerLogger.getInstance().error(iae.getMessage());
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, iae.getMessage());
        }
    }

}
