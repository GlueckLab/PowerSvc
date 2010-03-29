package edu.cudenver.bios.powersvc.representation;

import java.io.IOException;
import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.domain.SampleSizeResults;

public class SampleSizeXMLRepresentation extends DomRepresentation
{
    public SampleSizeXMLRepresentation(final SampleSizeResults results) throws IOException
    {
        super(MediaType.APPLICATION_XML);

        Document doc = getDocument();
        Element SampleSizeElem = createSampleSizeElement(doc, results);
        doc.appendChild(SampleSizeElem);
        doc.normalizeDocument();
    }

    public static Element createSampleSizeElement(Document doc, SampleSizeResults results) 
    {       
        Element SampleSizeElem = doc.createElement(PowerConstants.TAG_SAMPLESIZE);

        SampleSizeElem.setAttribute(PowerConstants.ATTR_SAMPLESIZE, Integer.toString(results.getSampleSize()));
        SampleSizeElem.setAttribute(PowerConstants.ATTR_POWER, Double.toString(results.getActualPower()));

        return SampleSizeElem;
    }
}