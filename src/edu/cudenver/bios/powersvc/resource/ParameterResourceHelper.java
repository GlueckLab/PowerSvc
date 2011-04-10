/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.powersvc.resource;

import java.util.ArrayList;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cudenver.bios.matrix.FixedRandomMatrix;
import edu.cudenver.bios.power.glmm.GLMMPowerConfidenceInterval.ConfidenceIntervalType;
import edu.cudenver.bios.power.glmm.GLMMTestFactory.Test;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.application.PowerLogger;

/**
 * Helper class for parsing GLMM parameters from a DOM tree.
 * 
 * @author Sarah Kreidler
 */
public class ParameterResourceHelper
{
	/**
	 * Parse a GLMMPowerParameters object from its XML representation
	 * 
	 * @param node root of the XML DOM tree
	 * @return GLMMPowerParameters object
	 * @throws ResourceException
	 */
    public static GLMMPowerParameters glmmPowerParametersFromDomNode(Node node)
    throws ResourceException
    {
    	GLMMPowerParameters params = new GLMMPowerParameters();

        // make sure the root node is a "glmmPowerParameters" tag
        if (!node.getNodeName().equals(PowerConstants.TAG_GLMM_POWER_PARAMETERS))
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid root node '" + node.getNodeName() + "' when parsing parameter object");

        // parse the random seed value if specified
        // TODO: API for specifying random seeds?
//        NamedNodeMap attrs = node.getAttributes();
//        if (attrs != null) 
//        {
//        	Node seedNode = attrs.getNamedItem(PowerConstants.ATTR_RANDOM_SEED);
//        	if (seedNode != null) params.setS
//        }
        
        /* process the child elements.  Includes matrix and list inputs */
        NodeList children = node.getChildNodes();
        if (children != null && children.getLength() > 0)
        {
            for (int i = 0; i < children.getLength(); i++)
            {
                Node child = children.item(i);
                if (PowerConstants.TAG_CONFIDENCE_INTERVAL.equals(child.getNodeName()))
                {
                	parseConfidenceInterval(params, child);
                }
                if (PowerConstants.TAG_FIXED_RANDOM_MATRIX.equals(child.getNodeName()))
                {
                    String matrixName = null;
                    NamedNodeMap matrixAttrs = child.getAttributes();
                    Node name = matrixAttrs.getNamedItem(PowerConstants.ATTR_NAME);
                    if (name != null) matrixName = name.getNodeValue();
                    
                    if (PowerConstants.MATRIX_TYPE_BETA.equals(matrixName))
                    {
                    	params.setBeta(fixedRandomMatrixFromDomNode(child));
                    }
                    else if (PowerConstants.MATRIX_TYPE_BETWEEN_CONTRAST.equals(matrixName))
                    {
                    	params.setBetweenSubjectContrast(fixedRandomMatrixFromDomNode(child));
                    }                	
                }
                else if (PowerConstants.TAG_MATRIX.equals(child.getNodeName()))
                {
                    // get the name of this matrix
                    String matrixName = null;
                    NamedNodeMap matrixAttrs = child.getAttributes();
                    Node name = matrixAttrs.getNamedItem(PowerConstants.ATTR_NAME);
                    if (name != null) matrixName = name.getNodeValue();
                    
                    // if we have a valid name, parse and save the matrix to the linear model parameters
                    if (matrixName != null && !matrixName.isEmpty())
                    {
                        RealMatrix matrix = matrixFromDomNode(child);

                        if (PowerConstants.MATRIX_TYPE_DESIGN.equals(matrixName))
                            params.setDesignEssence(matrix);
                        else if (PowerConstants.MATRIX_TYPE_THETA.equals(matrixName))
                            params.setTheta(matrix);
                        else if (PowerConstants.MATRIX_TYPE_WITHIN_CONTRAST.equals(matrixName))
                            params.setWithinSubjectContrast(matrix);
                        else if (PowerConstants.MATRIX_TYPE_SIGMA_ERROR.equals(matrixName))
                            params.setSigmaError(matrix);
                        else if (PowerConstants.MATRIX_TYPE_SIGMA_GAUSSIAN.equals(matrixName))
                            params.setSigmaGaussianRandom(matrix);
                        else if (PowerConstants.MATRIX_TYPE_SIGMA_OUTCOME.equals(matrixName))
                            params.setSigmaOutcome(matrix);
                        else if (PowerConstants.MATRIX_TYPE_SIGMA_OUTCOME_GAUSSIAN.equals(matrixName))
                            params.setSigmaOutcomeGaussianRandom(matrix);
                        else
                            PowerLogger.getInstance().warn("Ignoring Invalid matrix: " + matrixName);                    
                    }
                    else
                    {
                        PowerLogger.getInstance().warn("Ignoring unnamed matrix");
                    }

                }
                else if (PowerConstants.TAG_TEST_LIST.equals(child.getNodeName()))
                {
                	parseTestList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_ALPHA_LIST.equals(child.getNodeName()))
                {
                	parseAlphaList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_POWER_LIST.equals(child.getNodeName()))
                {
                	parsePowerList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_SAMPLE_SIZE_LIST.equals(child.getNodeName()))
                {
                	parseSampleSizeList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_BETA_SCALE_LIST.equals(child.getNodeName()))
                {
                	parseBetaScaleList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_SIGMA_SCALE_LIST.equals(child.getNodeName()))
                {
                	parseSigmaScaleList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_POWER_METHOD_LIST.equals(child.getNodeName()))
                {
                	parsePowerMethodList(params, child.getChildNodes());
                }
                else if (PowerConstants.TAG_QUANTILE_LIST.equals(child.getNodeName()))
                {
                	parseQuantileList(params, child.getChildNodes());
                }
                else 
                {
                    PowerLogger.getInstance().warn("Ignoring unknown tag while parsing parameters: " + child.getNodeName());
                }
            }
        }
 
        return params;    	
    }
    
    
    /**
     * Parse the confidence interval description from the confidenceInterval tag
     * 
     * @param params power parameter object
     * @param node DOM node for confidenceInterval tag 
     */
    private static void parseConfidenceInterval(GLMMPowerParameters params, Node node)
    throws ResourceException
    {
        NamedNodeMap attrs = node.getAttributes();
        Node type = attrs.getNamedItem(PowerConstants.ATTR_TYPE);
        // user must specifiy a type attribute for the CI tag to be recognized
        if (type != null)
        {
        	if (PowerConstants.CONFIDENCE_INTERVAL_BETA_KNOWN_EST_SIGMA.equals(type.getNodeValue()))
        	{
        		params.setConfidenceIntervalType(ConfidenceIntervalType.BETA_KNOWN_SIGMA_ESTIMATED);
        	}
        	else
        	{
        		// default to assuming both matrices are estimated
        		params.setConfidenceIntervalType(ConfidenceIntervalType.BETA_SIGMA_ESTIMATED);
        	}
        	
        	try
        	{
        		// get the lower tail alpha level
        		Node alphaLowerNode = attrs.getNamedItem(PowerConstants.ATTR_CI_ALPHA_LOWER);
        		if (alphaLowerNode != null)
        		{
        			double alphaLower = Double.parseDouble(alphaLowerNode.getNodeValue());
        			if (alphaLower > 0 && alphaLower < 0.5)
        			{
        				params.setAlphaLowerConfidenceLimit(alphaLower);
        			}
        			else 
        			{
        				PowerLogger.getInstance().warn("Ignoring invalid lower tail probability for confidence intervals [" 
        						+ alphaLower + "], using 0.025 instead");
        			}
        		}

        		// get the upper tail alpha level
        		Node alphaUpperNode = attrs.getNamedItem(PowerConstants.ATTR_CI_ALPHA_UPPER);
        		if (alphaUpperNode != null)
        		{
        			double alphaUpper = Double.parseDouble(alphaUpperNode.getNodeValue());
        			if (alphaUpper > 0 && alphaUpper < 0.5)
        			{
        				params.setAlphaUpperConfidenceLimit(alphaUpper);
        			}
        			else
        			{
        				PowerLogger.getInstance().warn("Ignoring invalid upper tail probability for confidence intervals [" 
        						+ alphaUpper + "], using 0.025 instead");
        			}
        		}
        	}
        	catch (Exception e)
        	{
        		// catch any number parsing problems for the alpha limits
        		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid alpha limits for confidence intervals");
        	}

            // NOTE: estimate sample size and design matrix rank are required for CI's to work,
            // so we throw an exception if either is missing
            // get the rank of the design matrix used for the estimates
            Node rankNode = attrs.getNamedItem(PowerConstants.ATTR_CI_ESTIMATES_RANK);
            int rank = 0;
            if (rankNode != null)
            {
            	try
            	{
            		rank = Integer.parseInt(rankNode.getNodeValue());
            		if (rank <= 0) throw new IllegalArgumentException("invalid rank");
            		params.setDesignMatrixRankForEstimates(rank);
            	}
            	catch (Exception e)
            	{
                	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        				"Invalid design matrix rank specified for confidence intervals");
            	}
            }
            else
            {
            	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
            			"No design matrix rank specified for confidence intervals");
            }
            
            Node sampleSizeNode = attrs.getNamedItem(PowerConstants.ATTR_CI_ESTIMATES_SAMPLE_SIZE);
            if (sampleSizeNode != null)
            {
            	try
            	{
            		int sampleSize = Integer.parseInt(sampleSizeNode.getNodeValue());
            		if (sampleSize < rank) throw new IllegalArgumentException("invalid sample size");
            		params.setSampleSizeForEstimates(sampleSize);
            	}
            	catch (Exception e)
            	{
                	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        				"Invalid sample size specified for confidence intervals");
            	}
            }
            else
            {
            	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
            			"No sample size specified for confidence intervals");
            }
        }
    }
    
    /**
     *  Parse the list of statistical tests to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parseTestList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
       			Node textNode = children.item(i).getFirstChild();
    			if (textNode != null)
    			{
    				String text = textNode.getNodeValue();
    				if (PowerConstants.TEST_HOTELLING_LAWLEY_TRACE.equals(text))
    				{
    	    			params.addTest(Test.HOTELLING_LAWLEY_TRACE);
    				}
    				else if (PowerConstants.TEST_PILLAI_BARTLETT_TRACE.equals(text))
    				{
    					params.addTest(Test.PILLAI_BARTLETT_TRACE);
    				}
    				else if (PowerConstants.TEST_WILKS_LAMBDA.equals(text))
    				{
    					params.addTest(Test.WILKS_LAMBDA);
    				}
    				else if (PowerConstants.TEST_UNIREP.equals(text))
    				{
    					params.addTest(Test.UNIREP);
    				}
    				else if (PowerConstants.TEST_UNIREP_BOX.equals(text))
    				{
    					params.addTest(Test.UNIREP_BOX);
    				}
    				else if (PowerConstants.TEST_UNIREP_GG.equals(text))
    				{
    					params.addTest(Test.UNIREP_GEISSER_GREENHOUSE);
    				}
    				else if (PowerConstants.TEST_UNIREP_HF.equals(text))
    				{
    					params.addTest(Test.UNIREP_HUYNH_FELDT);
    				}
    			}
    		}
    	}
    }

    /**
     *  Parse the list of alpha values to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parseAlphaList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			params.addAlpha(Double.parseDouble(textNode.getNodeValue()));
    		}
    	}
    }
    
    /**
     *  Parse the list of powers to be included with this calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parsePowerList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			params.addPower(Double.parseDouble(textNode.getNodeValue()));
    		}
    	}
    }
    
    /**
     *  Parse the list of per group sample sizes to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parseSampleSizeList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			params.addSampleSize(Integer.parseInt(textNode.getNodeValue()));
    		}
    	}
    }
    
    /**
     *  Parse the list of beta matrix scale factors to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parseBetaScaleList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			params.addBetaScale(Double.parseDouble(textNode.getNodeValue()));
    		}
    	}
    }
    
    /**
     *  Parse the list of sigma scale factors to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parseSigmaScaleList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			params.addSigmaScale(Double.parseDouble(textNode.getNodeValue()));
    		}
    	}
    }
    
    /**
     *  Parse the list of power methods to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parsePowerMethodList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			if (textNode != null)
    			{
    				String text = textNode.getNodeValue();
    				if (PowerConstants.POWER_METHOD_QUANTILE.equals(text))
    				{
    	    			params.addPowerMethod(GLMMPowerParameters.PowerMethod.QUANTILE_POWER);
    				}
    				else if (PowerConstants.POWER_METHOD_UNCONDITIONAL.equals(text))
    				{
    					params.addPowerMethod(GLMMPowerParameters.PowerMethod.UNCONDITIONAL_POWER);
    				}
    				else if (PowerConstants.POWER_METHOD_CONDITIONAL.equals(text))
    				{
    					params.addPowerMethod(GLMMPowerParameters.PowerMethod.CONDITIONAL_POWER);
    				}
    			}
    		}
    	}
    }
    
    /**
     *  Parse the list of quantiles to be included with this power calculation
     *  
     * @param params parameter object being built
     * @param children children of the testList tag
     */
    public static void parseQuantileList(GLMMPowerParameters params, NodeList children)
    {
    	if (children != null && params != null)
    	{
    		for(int i = 0; i < children.getLength(); i++)
    		{
    			Node textNode = children.item(i).getFirstChild();
    			params.addQuantile(Double.parseDouble(textNode.getNodeValue()));
    		}
    	}
    }
    
    /**
     *  Parse a fixed/random matrix from a DOM node tree
     *  @param node root node for fixed/random matrix
     */
    public static FixedRandomMatrix fixedRandomMatrixFromDomNode(Node node)
    throws ResourceException
    {
    	double[][] fixedData = null;
    	double[][] randomData = null;
    	boolean combineHorizontal = true;
    	
    	// determine whether the fixed/random pieces should be combined vertically or horizontally
        NamedNodeMap attrs = node.getAttributes();
    	Node combineAttr = attrs.getNamedItem(PowerConstants.ATTR_COMBINE_HORIZONTAL);
    	if (combineAttr != null) combineHorizontal = Boolean.parseBoolean(combineAttr.getNodeValue());
    	
    	// parse the fixed and random matrix data
        NodeList children = node.getChildNodes();
        if (children != null && children.getLength() > 0)
        {
            for (int i = 0; i < children.getLength(); i++)
            {
                Node child = children.item(i);  
                if (PowerConstants.TAG_MATRIX.equals(child.getNodeName()))
                {
                    String matrixName = null;
                    NamedNodeMap matrixAttrs = child.getAttributes();
                    Node name = matrixAttrs.getNamedItem(PowerConstants.ATTR_NAME);
                    if (name != null) 
                    {
                    	matrixName = name.getNodeValue();
                    	RealMatrix matrix = ParameterResourceHelper.matrixFromDomNode(child);
                    	
                    	if (PowerConstants.ATTR_FIXED.equals(matrixName))
                    	{
                    		fixedData = matrix.getData();
                    	}
                    	else if (PowerConstants.ATTR_RANDOM.equals(matrixName))
                    	{
                    		randomData = matrix.getData();
                    	}
                    }
                }
                else
                {
                    PowerLogger.getInstance().warn("Ignoring unknown fixed/random matrix child tag: " + child.getNodeName());
                }
            }
        }
    	
    	return new FixedRandomMatrix(fixedData, randomData, combineHorizontal);
    }
    
    /**
     * Parse a matrix from XML DOM.  The matrix should be specified as follows:
     * <p>
     * &lt;matrix type=(beta|theta|sigma|design|withinSubjectContrast|betweenSubjectContrast) &gt;
     * <br>&lt;row&gt;&lt;col&gt;number&lt;col/&gt;...&lt;/row&gt;
     * <br>...
     * <br>&lt;/matrix&gt;
     * 
     * @param node
     * @return matrix object
     * @throws ResourceException
     */
    public static RealMatrix matrixFromDomNode(Node node) throws ResourceException
    {        
        // make sure the root node is a matrix
        if (!node.getNodeName().equals(PowerConstants.TAG_MATRIX))
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid root node '" + node.getNodeName() + "' when parsing matrix object");
        
        // parse the rows / columns from the attribute list
        NamedNodeMap attrs = node.getAttributes();
        Node numRowsStr = attrs.getNamedItem(PowerConstants.ATTR_ROWS);
        int numRows = 0;
        if (numRowsStr != null) numRows = Integer.parseInt(numRowsStr.getNodeValue());

        Node numColsStr = attrs.getNamedItem(PowerConstants.ATTR_COLUMNS);
        int numCols = 0;
        if (numColsStr != null) numCols = Integer.parseInt(numColsStr.getNodeValue());
        
        // make sure we got a reasonable value for rows/columns
        if (numRows <= 0 || numCols <=0)
            throw new IllegalArgumentException("Invalid matrix rows/columns specified - must be positive integer");
            
        // create a placeholder matrix for storing the rows/columns
        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(numRows, numCols);
        
        // parse the children: should contain multiple row objects with col objects as children
        NodeList rows = node.getChildNodes();
        if (rows != null && rows.getLength() > 0)
        {
            for (int rowIndex = 0; rowIndex < rows.getLength() && rowIndex < numRows; rowIndex++)
            {
                Node row = rows.item(rowIndex);
                if (!PowerConstants.TAG_ROW.equals(row.getNodeName()))
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid row node '" + row.getNodeName() + "' when parsing matrix object");

                // get all of the columns for the current row and insert into a matrix
                NodeList columns = row.getChildNodes();
                if (columns != null && columns.getLength() > 0)
                {
                    for(int colIndex = 0; colIndex < columns.getLength() && colIndex < numCols; colIndex++)
                    {
                        Node colEntry = columns.item(colIndex);
                        String valStr = colEntry.getFirstChild().getNodeValue();
                        if (colEntry.hasChildNodes() && valStr != null && !valStr.isEmpty())
                        {
                            double val = Double.parseDouble(valStr);
                            matrix.setEntry(rowIndex, colIndex, val);
                        }
                        else
                        {
                            throw new IllegalArgumentException("Missing data in matrix [row=" + rowIndex + " col=" + colIndex + "]");
                        }
                    }
                }
            }
            
        }
        return matrix;
    }
    

}
