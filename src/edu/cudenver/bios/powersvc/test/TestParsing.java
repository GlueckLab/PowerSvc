package edu.cudenver.bios.powersvc.test;

import java.io.StringReader;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.domain.PowerInputs;
import edu.cudenver.bios.powersvc.resource.PowerResourceHelper;

import junit.framework.TestCase;

public class TestParsing extends TestCase
{
    private Document validGLMMPowerDoc = null;
    private static final String validGLMMPower = "<power><params alpha='0.05'>" +
    "<matrix name='beta' rows='3' columns='2'><r><c>1</c><c>0</c></r>" +
    "<r><c>0</c><c>1</c></r><r><c>2</c><c>1</c></r></matrix>" +
    "<matrix name='sigma' rows='3' columns='2'></matrix>" +
    "<matrix name='theta' rows='3' columns='2'></matrix>" +
    "<matrix name='withinSubjectContrast' rows='3' columns='2'></matrix>" +
    "<matrix name='betweenSubjectContrast' rows='3' columns='2'></matrix>" +
    "<essenceMatrix>" +
    "<rowMetaData><r reps='10' /><r reps='10' /><r reps='20' /></rowMetaData>" +
    "<columnMetaData><c type='fixed'/><c type='fixed' /><c type='random' mean='2' var='1' /></columnMetaData>" +
    "<matrix rows='3' columns='3'><r><c>1</c><c>0</c><c>0</c></r>" + 
    "<r><c>0</c><c>1</c><c>0</c></r>" +
    "<r><c>0</c><c>0</c><c>1</c></r>" +
    "</matrix></essenceMatrix>" + 
    "</params></power>";

    // data feed without name, retrieval has no serverpool key
    private Document invalidGLMMPowerDoc = null;
    private static final String invalidGLMMPower = "<power><params>" +
    "<matrix name='beta'></matrix>" +
    "<matrix name='sigma'></matrix>" +
    "<matrix name='beta'></matrix>" +
    "<matrix name='beta'></matrix>" +
    "<matrix name='beta'></matrix>" +
    "<matrix name='beta'></matrix>" + 
    "</params></power>";

    private Document validOneSampleStudentsTDoc = null;
    private static final String validOneSampleStudentsT = 
        "<power><params /></power>";

    private Document invalidOneSampleStudentsTDoc = null;
    private static final String invalidOneSampleStudentsT = 
        "<power><params /></power>";

    public void setUp()
    {
        try
        {
            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            validGLMMPowerDoc = builder.parse(new InputSource(new StringReader(validGLMMPower)));
            invalidGLMMPowerDoc = builder.parse(new InputSource(new StringReader(invalidGLMMPower)));
            validOneSampleStudentsTDoc = 
                builder.parse(new InputSource(new StringReader(validOneSampleStudentsT)));
            invalidOneSampleStudentsTDoc = 
                builder.parse(new InputSource(new StringReader(invalidOneSampleStudentsT)));
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
            PowerInputs inputs = 
                PowerResourceHelper.powerFromDomNode(PowerConstants.TEST_GLMM,
                        validGLMMPowerDoc.getDocumentElement());
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
            PowerInputs inputs = 
                PowerResourceHelper.powerFromDomNode(PowerConstants.TEST_GLMM,
                        invalidGLMMPowerDoc.getDocumentElement());
            fail();
        }
        catch(Exception e)
        {
            System.out.println("Caught exception on invalid GLMM:  " + e.getMessage());
            assertTrue(true);
        }
    }


}
