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
package edu.ucdenver.bios.powersvc.resource;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.cudenver.bios.power.GLMMPowerCalculator;
import edu.cudenver.bios.power.Power;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.ucdenver.bios.javastatistics.design.StudyDesign;

public class PowerServerResource extends ServerResource
implements PowerResource
{
	/**
	 * Calculate power for the specified study design
	 * 
	 * @param studyDesign study design object
	 * @return List of power objects for the study design
	 */
	@Override
	public List<Power> getPower(StudyDesign studyDesign)
	{
    	if (studyDesign == null) 
    		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
    				"Invalid study design");
    	
        try
        {
        	GLMMPowerParameters params = studyDesignToPowerParameters(studyDesign);
            // create the appropriate power calculator for this model
            GLMMPowerCalculator calculator = new GLMMPowerCalculator();
            // calculate the detecable difference results
            List<Power> results = calculator.getPower(params);

            return results;
        }
        catch (IllegalArgumentException iae)
        {
            PowerLogger.getInstance().error(iae.getMessage());
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, iae.getMessage());
        }
	}

	/**
	 * Calculate the total sample size for the specified study design.
	 * 
	 * @param studyDesign study design object
	 * @return List of power objects for the study design.  These will contain the total sample size
	 */
	@Override
	public List<Power> getSampleSize(StudyDesign studyDesign)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Calculate the detectable difference for the specified study design.
	 * 
	 * @param studyDesign study design object
	 * @return List of power objects for the study design.  These will contain the detectable difference
	 */
	@Override
	public List<Power> getDetectableDifference(StudyDesign studyDesign)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Convert a study design object into a power parameters object
	 * TODO: should be removed once modifications to java stats are complete
	 * 
	 * @param studyDesign
	 * @return
	 */
	private GLMMPowerParameters studyDesignToPowerParameters(StudyDesign studyDesign)
	{
		GLMMPowerParameters params = new GLMMPowerParameters();
		
		return params;
	}

}
