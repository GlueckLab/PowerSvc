package edu.cudenver.bios.powersvc.resource;

import java.io.IOException;
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

import edu.cudenver.bios.powersamplesize.Power;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.domain.PowerDescription;
import edu.cudenver.bios.powersvc.domain.PowerResults;
import edu.cudenver.bios.powersvc.representation.ErrorXMLRepresentation;
import edu.cudenver.bios.powersvc.representation.PowerXMLRepresentation;

public class PowerResource extends Resource
{
    private String modelName = null;

    public PowerResource(Context context, Request request, Response response) 
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
        DomRepresentation rep = new DomRepresentation(entity);

        try
        {
            // parse the power options and parameters from the entity body
            PowerDescription desc = PowerResourceHelper.powerFromDomNode(modelName, rep.getDocument().getDocumentElement());

            // create the appropriate power calculator for this model
            Power calculator = PowerResourceHelper.getCalculatorByModelName(modelName);
            // create a results object
            PowerResults results = new PowerResults();
            // calculate the power
            results.setPower(calculator.getCalculatedPower(desc.getParameters()));
            // if requested, add simulated power
            if (desc.isSimulated())
            {
                results.setSimulatedPower(calculator.getSimulatedPower(desc.getParameters(), 
                        desc.getSimulationIterations()));
            }
                       
            // build the response xml
            PowerXMLRepresentation response = new PowerXMLRepresentation(results);
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
