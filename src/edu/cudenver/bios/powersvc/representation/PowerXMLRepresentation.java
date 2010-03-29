package edu.cudenver.bios.powersvc.representation;

import java.io.IOException;
import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.domain.PowerResults;

public class PowerXMLRepresentation extends DomRepresentation
{    
    public PowerXMLRepresentation(final PowerResults power) throws IOException
    {
        super(MediaType.APPLICATION_XML);

        Document doc = getDocument();
        Element PowerElem = createPowerElement(doc, power);
        doc.appendChild(PowerElem);
        doc.normalizeDocument();
    }

    public static Element createPowerElement(Document doc, PowerResults power) 
    {       
        Element PowerElem = doc.createElement(PowerConstants.TAG_POWER);

        PowerElem.setAttribute(PowerConstants.ATTR_CALCULATED, Double.toString(power.getPower()));
        PowerElem.setAttribute(PowerConstants.ATTR_SIMULATED, Double.toString(power.getSimulatedPower()));

        // TODO: if essence matrix specified with random predictor, return full parameters ???
        return PowerElem;
    }
}

