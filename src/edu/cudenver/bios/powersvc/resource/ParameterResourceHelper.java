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
import edu.cudenver.bios.matrix.RandomColumnMetaData;
import edu.cudenver.bios.matrix.DesignEssenceMatrix;
import edu.cudenver.bios.matrix.RowMetaData;
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

        /* process the child elements.  Includes matrix and list inputs */
        NodeList children = node.getChildNodes();
        if (children != null && children.getLength() > 0)
        {
            for (int i = 0; i < children.getLength(); i++)
            {
                Node child = children.item(i);
                if (PowerConstants.TAG_ESSENCE_MATRIX.equals(child.getNodeName()))
                {
                    DesignEssenceMatrix essence = essenceMatrixFromDomNode(child);
                    params.setDesignEssence(essence);
                }
                else if (PowerConstants.TAG_FIXED_RANDOM_MATRIX.equals(child.getNodeName()))
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
                            params.setDesign(matrix);
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
    	    			params.addTest(GLMMPowerParameters.Test.HOTELLING_LAWLEY_TRACE);
    				}
    				else if (PowerConstants.TEST_PILLAI_BARTLETT_TRACE.equals(text))
    				{
    					params.addTest(GLMMPowerParameters.Test.PILLAI_BARTLETT_TRACE);
    				}
    				else if (PowerConstants.TEST_WILKS_LAMBDA.equals(text))
    				{
    					params.addTest(GLMMPowerParameters.Test.WILKS_LAMBDA);
    				}
    				else if (PowerConstants.TEST_UNIREP.equals(text))
    				{
    					params.addTest(GLMMPowerParameters.Test.UNIREP);
    				}
    				else if (PowerConstants.TEST_UNIREP_BOX.equals(text))
    				{
    					params.addTest(GLMMPowerParameters.Test.UNIREP_BOX);
    				}
    				else if (PowerConstants.TEST_UNIREP_GG.equals(text))
    				{
    					params.addTest(GLMMPowerParameters.Test.UNIREP_GEISSER_GREENHOUSE);
    				}
    				else if (PowerConstants.TEST_UNIREP_HF.equals(text))
    				{
    					params.addTest(GLMMPowerParameters.Test.UNIREP_HUYNH_FELDT);
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
     * Parse an essence matrix from a DOM tree
     * 
     * @param node root node of the DOM tree
     * @return essence matrix object
     * @throws IllegalArgumentException
     */
    public static DesignEssenceMatrix essenceMatrixFromDomNode(Node node)
    throws ResourceException
    {
        DesignEssenceMatrix essence = null;
        double[][] fixedData = null;
        double[][] randomData = null;
        RowMetaData[] rmd = null;
        RandomColumnMetaData[] cmd = null;
        Node seed = null;
        
        // parse the random seed value if specified
        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) seed = attrs.getNamedItem(PowerConstants.ATTR_RANDOM_SEED);
        
        // parse the matrix data, row meta data, and column meta data
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
                else if (PowerConstants.TAG_ROW_META_DATA.equals(child.getNodeName()))
                {
                    rmd = ParameterResourceHelper.rowMetaDataFromDomNode(child);
                }
                else if (PowerConstants.TAG_RANDOM_COLUMN_META_DATA.equals(child.getNodeName()))
                {
                    cmd = ParameterResourceHelper.randomColumnMetaDataFromDomNode(child);
                }
                else
                {
                    PowerLogger.getInstance().warn("Ignoring unknown essence matrix child tag: " + child.getNodeName());
                }
            }
        }
        
        // now that we're done parsing, build the essence matrix object
        essence =  new DesignEssenceMatrix(fixedData, rmd, randomData, cmd); 
        if (seed != null)
        {
        	essence.setRandomSeed(Integer.parseInt(seed.getNodeValue()));
        }
        
        return essence;
        
    }
    
    /**
     * Parse a row meta data object from a DOM node
     * 
     * @param node
     * @return array of RowMetaData objects
     */
    public static RowMetaData[] rowMetaDataFromDomNode(Node node)
    {
        ArrayList<RowMetaData> metaDataList = new ArrayList<RowMetaData>();
        
        NodeList children = node.getChildNodes();
        if (children != null && children.getLength() > 0)
        {
            for (int i = 0; i < children.getLength(); i++)
            {
                RowMetaData rmd = new RowMetaData();
                Node child = children.item(i);
                if (PowerConstants.TAG_ROW.equals(child.getNodeName()))
                {
                    NamedNodeMap attrs = child.getAttributes();
                    
                    Node ratio = attrs.getNamedItem(PowerConstants.ATTR_RATIO);
                    if (ratio != null) rmd.setRatio(Integer.parseInt(ratio.getNodeValue()));
                }
                else
                {
                    PowerLogger.getInstance().warn("Ignoring unknown tag while parsing row meta data: " + child.getNodeName());
                }
                metaDataList.add(rmd);
            }
        }
        
        return (RowMetaData[]) metaDataList.toArray(new RowMetaData[metaDataList.size()]);
    }

    /**
     * Parse an array of column meta data from a DOM tree
     * 
     * @param node 
     * @return list of column meta data
     */
    public static RandomColumnMetaData[] randomColumnMetaDataFromDomNode(Node node)
    {
        ArrayList<RandomColumnMetaData> metaDataList = new ArrayList<RandomColumnMetaData>();
        
        NodeList children = node.getChildNodes();
        if (children != null && children.getLength() > 0)
        {
            for (int i = 0; i < children.getLength(); i++)
            {
                Node child = children.item(i);
                if (PowerConstants.TAG_COLUMN.equals(child.getNodeName()))
                {
                    NamedNodeMap attrs = child.getAttributes();
                    
                    Node mean = attrs.getNamedItem(PowerConstants.ATTR_MEAN);
                    Node variance = attrs.getNamedItem(PowerConstants.ATTR_VARIANCE);

                    if (mean != null && variance != null)
                    {
                    	metaDataList.add(new RandomColumnMetaData(Double.parseDouble(mean.getNodeValue()),
                    			Double.parseDouble(variance.getNodeValue())));
                    }
                }
                else
                {
                    PowerLogger.getInstance().warn("Ignoring unknown tag while parsing row meta data: " + child.getNodeName());
                }
            }
        }
        
        return (RandomColumnMetaData[]) metaDataList.toArray(new RandomColumnMetaData[metaDataList.size()]);
    }
    
    /**
     * 
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
