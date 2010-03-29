package edu.cudenver.bios.powersvc.resource;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jfree.chart.JFreeChart;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.cudenver.bios.powersamplesize.graphics.PowerCurveBuilder;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.domain.PowerCurveDescription;
import edu.cudenver.bios.powersvc.domain.PowerDescription;
import edu.cudenver.bios.powersvc.representation.ErrorXMLRepresentation;
import edu.cudenver.bios.powersvc.representation.PowerCurveRepresentation;

public class PowerCurveResource extends Resource
{
    private String modelName = null;

    public PowerCurveResource(Context context, Request request, Response response) 
    {
        super(context, request, response);

        modelName = (String) request.getAttributes().get("modelName");

        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    @Override
    public boolean allowGet()
    {
        return false;
    }

    @Override
    public boolean allowPut()
    {
        return false;
    }

    @Override
    public boolean allowPost() 
    {
        return  true;
    }

    @Override 
    public void acceptRepresentation(Representation entity)
    {
        Form rep = new Form(entity);

        try
        {
            Parameter curveRequest = rep.getFirst("curveRequest");
            if (curveRequest == null || curveRequest.getValue() == null) 
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "Missing curve request parameters");
            // use a document builder to parse the form parameter as a xml document
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document curveRequestDoc = docBuilder.parse(new InputSource(new StringReader(curveRequest.getValue())));
            // parse the power options and parameters from the entity body
            PowerDescription desc = 
                PowerResourceHelper.powerFromDomNode(modelName, 
                        curveRequestDoc.getDocumentElement());
            
            // create a power curve 
            PowerCurveDescription curveDesc = desc.getCurveDescription();
            if (curveDesc == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid curve description");

            PowerCurveBuilder builder =
                new PowerCurveBuilder(PowerResourceHelper.getCalculatorByModelName(modelName), 
                        SampleSizeResourceHelper.getCalculatorByModelName(modelName));
            builder.setTitle(curveDesc.getTitle());
            builder.setXaxisLabel(curveDesc.getXAxisLabel());
            builder.setYaxisLabel(curveDesc.getYAxisLabel());
            JFreeChart powerCurve = builder.getPowerCurve(desc.getParameters());           
            
            // build the response xml
            PowerCurveRepresentation response = 
                new PowerCurveRepresentation(powerCurve, curveDesc.getWidth(), curveDesc.getHeight());
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
        catch (SAXException se)
        {
            PowerLogger.getInstance().error(se.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(se.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        catch (ParserConfigurationException pe)
        {
            PowerLogger.getInstance().error(pe.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(pe.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }

}
