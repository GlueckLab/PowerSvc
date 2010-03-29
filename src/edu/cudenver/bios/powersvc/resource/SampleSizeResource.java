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
import edu.cudenver.bios.powersamplesize.SampleSize;
import edu.cudenver.bios.powersamplesize.parameters.PowerSampleSizeParameters;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.domain.PowerSampleSizeDescription;
import edu.cudenver.bios.powersvc.domain.SampleSizeResults;
import edu.cudenver.bios.powersvc.representation.ErrorXMLRepresentation;
import edu.cudenver.bios.powersvc.representation.SampleSizeXMLRepresentation;

public class SampleSizeResource extends Resource
{
    public SampleSizeResource(Context context, Request request, Response response) 
    {
        super(context, request, response);
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
            // parse the sample size options and parameters from the entity body
            PowerSampleSizeDescription desc = SampleSizeResourceHelper.sampleSizeFromDomNode(rep.getDocument().getDocumentElement());
            PowerSampleSizeParameters params = desc.getParameters();
            // create the appropriate sample size calculator for this model
            SampleSize calculator = SampleSizeResourceHelper.getCalculatorByModelName(desc.getModelName());
            // create a results object
            SampleSizeResults results = new SampleSizeResults();
            // calculate the sample size
            int sampleSize = calculator.getSampleSize(params);
            results.setSampleSize(sampleSize);
            // calculate the actual power associated with the sample size
            SampleSizeResourceHelper.updateParameters(desc.getModelName(), params, sampleSize);
            Power powerCalc = PowerResourceHelper.getCalculatorByModelName(desc.getModelName());
            results.setActualPower(powerCalc.getCalculatedPower(params));

            // build the response xml
            SampleSizeXMLRepresentation response = new SampleSizeXMLRepresentation(results);
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
