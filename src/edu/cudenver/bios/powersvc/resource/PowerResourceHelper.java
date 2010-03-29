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
import edu.cudenver.bios.powersvc.domain.PowerCurveDescription;
import edu.cudenver.bios.powersvc.domain.PowerDescription;

public class PowerResourceHelper
{
    public static PowerDescription powerFromDomNode(Node node) 
    throws ResourceException
    {
        PowerDescription desc = new PowerDescription();
                
        if (!PowerConstants.TAG_POWER.equals(node.getNodeName()))
            throw new IllegalArgumentException("Invalid root node '" + node.getNodeName() + "' when parsing power object");

        // get any power options from the <power> tag
        try
        {
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing model name when parsing power object");

            // make sure a model name is specified as an attribute
            Node modelNameNode = attrs.getNamedItem(PowerConstants.ATTR_MODEL);
            if (modelNameNode != null && !modelNameNode.getNodeValue().isEmpty()) 
                desc.setModelName(modelNameNode.getNodeValue());
            else
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing model name when parsing power object");

            /* parse optional arguments */
            // simulated=true|false, if true includes simulation of power
            Node sim = attrs.getNamedItem(PowerConstants.ATTR_SIMULATED);
            if (sim != null) desc.setSimulated(Boolean.parseBoolean(sim.getNodeValue()));
            // simulation iterations, indicates the number of iterations of a simulation to run
            Node iter = attrs.getNamedItem(PowerConstants.ATTR_SIMULATION_SIZE);
            if (iter != null) desc.setSimulationIterations(Integer.parseInt(iter.getNodeValue()));       
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
                Node child = children.item(i);
                if (PowerConstants.TAG_CURVE.equals(child.getNodeName()))
                {
                    PowerCurveDescription curveDesc = ParameterResourceHelper.powerCurveFromDomNode(child);
                    desc.setCurveDescription(curveDesc);
                }
                else if (PowerConstants.TAG_PARAMS.equals(child.getNodeName()))
                {
                    // parse the appropriate sample size parameters depending on the type of model
                    PowerSampleSizeParameters params = 
                        ParameterResourceHelper.powerSampleSizeParametersFromDomNode(desc.getModelName(), children.item(i));
                    if (params != null) desc.setParameters(params);
                }
            }
        }
                
        return desc;
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
