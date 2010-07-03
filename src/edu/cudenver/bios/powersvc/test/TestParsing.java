package edu.cudenver.bios.powersvc.test;

import java.io.StringReader;
import java.util.ArrayList;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

import edu.cudenver.bios.power.Power;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.cudenver.bios.powersvc.representation.GLMMPowerListXMLRepresentation;
import edu.cudenver.bios.powersvc.resource.ParameterResourceHelper;

import junit.framework.TestCase;

public class TestParsing extends TestCase
{
    private Document validGLMMPowerDoc = null;
    private static final String validGLMMPower = "<glmmPowerParameters>" +
    "<testList><v>hlt</v><v>wl</v></testList>" +
    "<alphaList><v>0.05</v><v>0.01</v></alphaList>" +
    "<sigmaScaleList><v>64</v></sigmaScaleList>" +
    "<sampleSizeList><v>200</v></sampleSizeList>" +
    "<betaScaleList><v>10</v></betaScaleList>" +
    "<essenceMatrix>" +
    "<rowMetaData><r ratio='1' /></rowMetaData>" +
    "<columnMetaData><c type='fixed' /></columnMetaData>" + 
    "<matrix name='design' rows='1' columns='1'><r><c>1</c></r></matrix>" +
    "</essenceMatrix>" +
    "<matrix name='betweenSubjectContrast' rows='1' columns='1'><r><c>1</c></r></matrix>" +
    "<matrix name='withinSubjectContrast' rows='2' columns='2'>" +
    "<r><c>1</c></r><r><c>-1</c></r></matrix>" +
    "<matrix name='theta' rows='1' columns='1'><r><c>0</c></r></matrix>" +
    "<matrix name='beta' rows='1' columns='2'><r><c>1</c><c>0</c></r></matrix>" +
    "<matrix name='sigmaError' rows='2' columns='2'>" + 
    "<r><c>1</c><c>0</c></r><r><c>0</c><c>1</c></r></matrix>" +
    "</glmmPowerParameters>";


    // data feed without name, retrieval has no serverpool key
    private Document invalidGLMMPowerDoc = null;
    private static final String invalidGLMMPower = "<glmmPowerParameters>" +
    "<testList><v>hlt</v></testList>" +
    "<alphaList><v>0.05</v><v>0.01</v></alphaList>" +
    "<sigmaScaleList><v>64</v></sigmaScaleList>" +
    "<sampleSizeList><v>200</v></sampleSizeList>" +
    "<betaScaleList><v>10</v></betaScaleList>" +
    "<essenceMatrix>" +
    "<rowMetaData><r ratio='1' /></rowMetaData>" +
    "<columnMetaData><c type='fixed' /></columnMetaData>" + 
    "<matrix name='design' rows='1' columns='1'><r><c>1</c></r></matrix>" +
    "</essenceMatrix>" +
    "<matrix name='betweenSubjectContrast'></matrix>" +
    "<matrix name='withinSubjectContrast' rows='2' columns='2'>" +
    "<r><c>1</c></r><r><c>-1</c></r></matrix>" +
    "<matrix name='theta' rows='1' columns='1'><r><c>0</c></r></matrix>" +
    "<matrix name='beta' rows='1' columns='2'><r><c>1</c><c>0</c></r></matrix>" +
    "<matrix name='sigmaError' rows='2' columns='2'>" + 
    "<r><c>1</c><c>0</c></r><r><c>0</c><c>1</c></r></matrix>" +
    "</glmmPowerParameters>";

    public void setUp()
    {
        try
        {
            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            validGLMMPowerDoc = builder.parse(new InputSource(new StringReader(validGLMMPower)));
            invalidGLMMPowerDoc = builder.parse(new InputSource(new StringReader(invalidGLMMPower)));
        }
        catch (Exception e)
        {
            fail();
        }
    }

    public void testValidPowerGLMM()
    {
        try
        {
            GLMMPowerParameters params = 
                ParameterResourceHelper.glmmPowerParametersFromDomNode(validGLMMPowerDoc.getDocumentElement());
            System.out.println("Valid GLMM inputs parsed successfully");
            assertTrue(true);
        }
        catch(Exception e)
        {
            System.out.println("Exception during valid GLMM parsing: " + e.getMessage());
            e.printStackTrace();
            fail();
        }
    }

    public void testInvalidPowerGLMM()
    {
        try
        {
            GLMMPowerParameters params = 
                ParameterResourceHelper.glmmPowerParametersFromDomNode(invalidGLMMPowerDoc.getDocumentElement());
            System.out.println("Valid GLMM inputs parsed successfully");
            fail();
        }
        catch(Exception e)
        {
            System.out.println("Caught exception on invalid GLMM:  " + e.getMessage());
            assertTrue(true);
        }
    }

}
