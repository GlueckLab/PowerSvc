package edu.cudenver.bios.powersvc.resource;

import org.apache.commons.math.linear.RealMatrix;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cudenver.bios.matrix.EssenceMatrix;
import edu.cudenver.bios.powersamplesize.SampleSize;
import edu.cudenver.bios.powersamplesize.SampleSizeGLMM;
import edu.cudenver.bios.powersamplesize.SampleSizeOneSampleStudentsT;
import edu.cudenver.bios.powersamplesize.parameters.LinearModelPowerSampleSizeParameters;
import edu.cudenver.bios.powersamplesize.parameters.PowerSampleSizeParameters;
import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.domain.PowerCurveDescription;
import edu.cudenver.bios.powersvc.domain.PowerSampleSizeDescription;

public class SampleSizeResourceHelper
{

    public static PowerSampleSizeDescription sampleSizeFromDomNode(Node node) 
    throws ResourceException
    {
        PowerSampleSizeDescription desc = new PowerSampleSizeDescription();
                
        // make sure a model name is specified as an attribute
        NamedNodeMap attrs = node.getAttributes();
        if (attrs == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing model name when parsing sample size object");
        Node modelNameNode = attrs.getNamedItem(PowerConstants.ATTR_MODEL);
        if (modelNameNode != null && !modelNameNode.getNodeValue().isEmpty()) 
            desc.setModelName(modelNameNode.getNodeValue());
        else
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing model name when parsing sample size object");

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
    
    public static SampleSize getCalculatorByModelName(String modelName)
    throws IllegalArgumentException
    {
        if (modelName == null || modelName.isEmpty())
            throw new IllegalArgumentException("No model name specified");
        
        if (modelName.equals(PowerConstants.TEST_ONE_SAMPLE_STUDENT_T))
        {
            return new SampleSizeOneSampleStudentsT();
        }
        else if (modelName.equals(PowerConstants.TEST_GLMM))
        {
            return new SampleSizeGLMM();
        }
        else
        {
            throw new IllegalArgumentException("Invalid model name: " + modelName);
        }
    }
    
    public static void updateParameters(String modelName, PowerSampleSizeParameters params,
            int sampleSize)
    {
        params.setSampleSize(sampleSize);

        // we have to create a new design matrix to get the actual power for a glm
        if (modelName.equals(PowerConstants.TEST_GLMM))
        {
            LinearModelPowerSampleSizeParameters lmParams =
                (LinearModelPowerSampleSizeParameters) params;
            RealMatrix design = lmParams.getDesignEssence().getFullDesignMatrix(sampleSize);
            lmParams.setDesign(design);
        }
    }
    
    /**
     * Adjusts the calculated sample size by rounding to an integer.
     * 
     *  For linear models, also adjusts to match the ratio of group sizes 
     *  specified in the design essence matrix
     *  
     * @param sampleSize
     * @return
     */
    public static int adjustSampleSize(double sampleSize, String modelName,
            PowerSampleSizeParameters params)
    {
        int adjSize = (int) Math.ceil(sampleSize);
        
        if (PowerConstants.TEST_GLMM.equals(modelName))
        {
            LinearModelPowerSampleSizeParameters lmParams =
                (LinearModelPowerSampleSizeParameters) params;
            EssenceMatrix em = lmParams.getDesignEssence();
            // calculate the minimum possible sample size for this design matrix
            // based on the ratio of group sizes
            // for example, if the ratio is 1:2:1, then the minimum sample size is 4
            int minSampleSize = 0;
            for(int i = 0; i < em.getRowDimension(); i++)
                minSampleSize += em.getRowMetaData(i).getRepetitions();
            // increment the calculated sample size until we get a value
            // that is a multiple of the minimum sample size
            for(;adjSize % minSampleSize != 0; adjSize++);
        }
        
        return adjSize;
    }
}
