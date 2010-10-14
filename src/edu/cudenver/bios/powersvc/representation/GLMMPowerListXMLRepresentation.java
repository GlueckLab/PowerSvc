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
package edu.cudenver.bios.powersvc.representation;

import java.io.IOException;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cudenver.bios.power.GLMMPower;
import edu.cudenver.bios.power.Power;
import edu.cudenver.bios.powersvc.application.PowerConstants;

/**
 * Class which converts a list of GLMM power results to an XML DOM
 * 
 * @author Sarah Kreidler
 */
public class GLMMPowerListXMLRepresentation extends DomRepresentation
{

	/**
	 * Create a Representation object from a list of GLMM power results.
	 * The restlet framework will write this information as the response body 
	 * 
	 * @param glmmPowers list of glmm power objects
	 * @throws IOException
	 */
    public GLMMPowerListXMLRepresentation(List<Power> glmmPowers) throws IOException 
    {
        super(MediaType.APPLICATION_XML);
        
        Document doc = getDocument();
        Element powerListElem = createGLMMPowerListElement(doc, glmmPowers);
        doc.appendChild(powerListElem);
        doc.normalizeDocument();
    }
    
    /**
     * Create the GLMM power list DOM element
     * 
     * @param doc DOM document object
     * @param powers list of GLMM powers
     * @return
     */
    private Element createGLMMPowerListElement(Document doc, List<Power> powers) 
    {
        // build extraction rule list
        Element powerListElem = doc.createElement(PowerConstants.TAG_POWER_LIST);
        powerListElem.setAttribute(PowerConstants.ATTR_COUNT, Integer.toString(powers.size()));
        for (Power power: powers) 
        {
        	GLMMPower glmmPower = (GLMMPower) power;
            Element powerElem = GLMMPowerXMLRepresentation.createGLMMPowerElement(doc, glmmPower);
            powerListElem.appendChild(powerElem);
        }
        return powerListElem;
    }

}
