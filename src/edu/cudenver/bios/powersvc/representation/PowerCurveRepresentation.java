package edu.cudenver.bios.powersvc.representation;

import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;

import edu.cudenver.bios.powersvc.application.PowerLogger;

public class PowerCurveRepresentation extends OutputRepresentation
{
    private JFreeChart chart;
    private int width;
    private int height;
    
    public PowerCurveRepresentation(JFreeChart chart, int width, int height)
    {
        super(MediaType.IMAGE_JPEG);
        this.chart = chart;
        this.width = width;
        this.height = height;
    }

    public void write(OutputStream os)
    {
        try
        {
            ChartUtilities.writeChartAsJPEG(os, chart, width, height);
        }
        catch (IOException e)
        {
            // TODO: best way to handle this error?
            PowerLogger.getInstance().error("IOException when writing power curve image: " + e.getMessage());
        }
    }
}
