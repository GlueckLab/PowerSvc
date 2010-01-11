package edu.cudenver.bios.powersvc.resource;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import edu.cudenver.bios.powersvc.application.PowerLogger;
import edu.cudenver.bios.powersvc.representation.ErrorXMLRepresentation;

/**
 * Resource which returns form data with an application/download
 * header to force the Save As dialog in the browser.
 * (Could not find a browser independent method for this on the
 * client-side)
 * 
 * @author Sarah Kreidler
 *
 */
public class SaveAsResource extends Resource
{

    public SaveAsResource(Context context, Request request, Response response) 
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
        try
        {
            // build the response xml
        	Form form = new Form(entity);
        	String filename = form.getFirstValue("filename");
        	if (filename == null || filename.isEmpty()) filename = "out.xml";
        	String format = form.getFirstValue("format");
        	String data = form.getFirstValue("data");
        	// TODO: format to pdf, word, ppt?
        	if (data != null)
        	{
        	    if (format != null)
        	    {
        	        
        	    }
        	    else
        	    {
                    PowerLogger.getInstance().warn("No format specified, returning data as xml");
        	        getResponse().setEntity(new StringRepresentation(data));
        	    }
        	}
        	else
        	{
        		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No data specified");
        	}
            Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
            if (responseHeaders == null)  
            {  
            	responseHeaders = new Form();  
            	getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
            }  
            responseHeaders.add("Content-type", "application/force-download");
            responseHeaders.add("Content-disposition", "attachment; filename=" + filename);
            
            getResponse().setStatus(Status.SUCCESS_CREATED);
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
