package edu.cudenver.bios.powersvc.representation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.jfree.chart.ChartUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cudenver.bios.powersvc.application.PowerConstants;
import edu.cudenver.bios.powersvc.domain.PowerCurveResults;

public class PowerCurveRepresentation
{
    public static Element createPowerCurveElement(Document doc, PowerCurveResults results)
    throws IOException
    {
        Element curveElem = doc.createElement(PowerConstants.TAG_CURVE_IMG);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Base64OutputStream out = new Base64OutputStream(bytes);
        ChartUtilities.writeChartAsJPEG(out, results.getCurve(), results.getWidth(), results.getHeight());
        bytes.flush();
        curveElem.appendChild(doc.createCDATASection(bytes.toString()));
        return curveElem;
    }
}
