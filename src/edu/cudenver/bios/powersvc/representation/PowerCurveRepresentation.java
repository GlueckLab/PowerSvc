package edu.cudenver.bios.powersvc.representation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.cudenver.bios.powersvc.application.PowerConstants;

public class PowerCurveRepresentation
{
    public static Element createPowerCurveElement(Document doc, JFreeChart curve,
            int width, int height)
    throws IOException
    {
        Element curveElem = doc.createElement(PowerConstants.TAG_CURVE_IMG);
        
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Base64OutputStream out = new Base64OutputStream(bytes);
        ChartUtilities.writeChartAsJPEG(out, curve, width, height);
        curveElem.appendChild(doc.createCDATASection(bytes.toString()));
        return curveElem;
    }
}
