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

import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cudenver.bios.power.GLMMPower;
import edu.cudenver.bios.powersvc.application.PowerConstants;

/**
 * Class which converts a GLMM Power object to an XML DOM
 * 
 *  @author Sarah Kreidler
 */
public class GLMMPowerXMLRepresentation extends DomRepresentation
{
	/**
	 * Constructor
	 * @param power GLMM power object
	 * @throws IOException
	 */
    public GLMMPowerXMLRepresentation(GLMMPower power) throws IOException 
    {
        super(MediaType.APPLICATION_XML);
        
        Document doc = getDocument();
        Element extractionRuleElem = createGLMMPowerElement(doc, power);
        doc.appendChild(extractionRuleElem);
        doc.normalizeDocument();
    }
    
    /**
     * Create a DOM element from a GLMM power object.  
     * 
     * @param doc the DOM document
     * @param power the GLMM power object
     * @return DOM node for a single power result
     */
    public static Element createGLMMPowerElement(Document doc, GLMMPower power) 
    {
        Element glmmPowerElem = doc.createElement(PowerConstants.TAG_GLMM_POWER);
        
        switch (power.getTest())
        {
        case HOTELLING_LAWLEY_TRACE:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_HOTELLING_LAWLEY_TRACE);
        	break;
        case PILLAI_BARTLETT_TRACE:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_PILLAI_BARTLETT_TRACE);
        	break;
        case WILKS_LAMBDA:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_WILKS_LAMBDA);
        	break;
        case UNIREP:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_UNIREP);
        	break;
        case UNIREP_BOX:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_UNIREP_BOX);
        	break;
        case UNIREP_GEISSER_GREENHOUSE:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_UNIREP_GG);
        	break;
        case UNIREP_HUYNH_FELDT:
            glmmPowerElem.setAttribute(PowerConstants.ATTR_TEST, PowerConstants.TEST_UNIREP_HF);
        	break;
        }
        glmmPowerElem.setAttribute(PowerConstants.ATTR_ALPHA, Double.toString(power.getAlpha()));
        glmmPowerElem.setAttribute(PowerConstants.ATTR_NOMINAL_POWER, Double.toString(power.getNominalPower()));
        glmmPowerElem.setAttribute(PowerConstants.ATTR_ACTUAL_POWER, Double.toString(power.getActualPower()));
        glmmPowerElem.setAttribute(PowerConstants.ATTR_SAMPLE_SIZE, Integer.toString(power.getTotalSampleSize()));
        glmmPowerElem.setAttribute(PowerConstants.ATTR_BETA_SCALE, Double.toString(power.getBetaScale()));
        glmmPowerElem.setAttribute(PowerConstants.ATTR_SIGMA_SCALE, Double.toString(power.getSigmaScale()));

        // add power method
        switch (power.getPowerMethod())
        {
        case CONDITIONAL_POWER:
        	glmmPowerElem.setAttribute(PowerConstants.ATTR_POWER_METHOD, 
        			PowerConstants.POWER_METHOD_CONDITIONAL);
        	break;
        case UNCONDITIONAL_POWER:
        	glmmPowerElem.setAttribute(PowerConstants.ATTR_POWER_METHOD, 
        			PowerConstants.POWER_METHOD_UNCONDITIONAL);
        	break;
        case QUANTILE_POWER:
        	glmmPowerElem.setAttribute(PowerConstants.ATTR_POWER_METHOD, 
        			PowerConstants.POWER_METHOD_QUANTILE);
        	break;
        }
        
        // add quantile if specified
        if (!Double.isNaN(power.getQuantile()))
        {
        	glmmPowerElem.setAttribute(PowerConstants.ATTR_QUANTILE, 
        			Double.toString(power.getQuantile()));
        }

        return glmmPowerElem;
    }

}
