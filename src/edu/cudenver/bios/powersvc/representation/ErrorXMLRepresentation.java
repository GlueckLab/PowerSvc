package edu.cudenver.bios.powersvc.representation;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cudenver.bios.powersvc.application.PowerConstants;

/**
 * XML representation of an error message.  
 * Avoids using server default and allows easier parsing/presentation
 * of error message on the client side
 * 
 * @author Sarah Kreidler
 *
 */
public class ErrorXMLRepresentation extends DomRepresentation
{
    /**
     * Create an XML representation of the specified error message
     * 
     * @param msg
     * @throws IOException
     */
    public ErrorXMLRepresentation(String msg) throws IOException
    {
        super(MediaType.APPLICATION_XML);
        
        Document doc = getDocument();
        Element errorElem = doc.createElement(PowerConstants.TAG_ERROR);
        errorElem.appendChild(doc.createTextNode(msg));
        doc.appendChild(errorElem);
        doc.normalizeDocument();
    }
}
