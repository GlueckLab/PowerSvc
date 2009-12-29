package edu.cudenver.bios.powersvc.resource;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cudenver.bios.powersamplesize.Power;
import edu.cudenver.bios.powersamplesize.PowerGLMM;
import edu.cudenver.bios.powersamplesize.PowerOneSampleStudentsT;
import edu.cudenver.bios.powersamplesize.parameters.PowerSampleSizeParameters;
import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.domain.PowerInputs;

public class PowerResourceHelper
{
    public static PowerInputs powerFromDomNode(String modelName, Node node) 
    throws ResourceException
    {
        PowerInputs inputs = new PowerInputs();
                
        if (!PowerConstants.TAG_POWER.equals(node.getNodeName()))
            throw new IllegalArgumentException("Invalid root node '" + node.getNodeName() + "' when parsing power object");

        // get any power options from the <power> tag
        try
        {
            NamedNodeMap attrs = node.getAttributes();
            if (attrs != null)
            {
                /* parse optional arguments */
                
                // simulated=true|false, if true includes simulation of power
                Node sim = attrs.getNamedItem(PowerConstants.ATTR_SIMULATED);
                if (sim != null) inputs.setSimulated(Boolean.parseBoolean(sim.getNodeValue()));
                // simulation iterations, indicates the number of iterations of a simulation to run
                Node iter = attrs.getNamedItem(PowerConstants.ATTR_SIMULATION_SIZE);
                if (iter != null) inputs.setSimulationIterations(Integer.parseInt(iter.getNodeValue()));
                // curve=true|false, if true a power curve will be generated
                Node curve = attrs.getNamedItem(PowerConstants.ATTR_CURVE);
                if (curve != null) inputs.setCurve(Boolean.parseBoolean(curve.getNodeValue()));
            }
        }
        catch (Exception e)
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        
        // parse the parameters - depend on the type of model
        NodeList children = node.getChildNodes();
        if (children != null && children.getLength() > 0)
        {
            for (int i = 0; i < children.getLength(); i++)
            {
                // parse the appropriate sample size parameters depending on the type of model
                PowerSampleSizeParameters params = 
                    ParameterResourceHelper.powerSampleSizeParametersFromDomNode(modelName, children.item(i));
                if (params != null) inputs.setParameters(params);
            }
        }
                
        return inputs;
    }
    
    public static Power getCalculatorByModelName(String modelName)
    throws IllegalArgumentException
    {
        if (modelName == null || modelName.isEmpty())
            throw new IllegalArgumentException("No model name specified");
        
        if (modelName.equals(PowerConstants.TEST_ONE_SAMPLE_STUDENT_T))
        {
            return new PowerOneSampleStudentsT();
        }
        else if (modelName.equals(PowerConstants.TEST_GLMM))
        {
            return new PowerGLMM();
        }
        else
        {
            throw new IllegalArgumentException("Invalid model name: " + modelName);
        }
    }
    

}
