package edu.cudenver.bios.powersvc.domain;

import org.jfree.chart.JFreeChart;

public class PowerCurveResults
{
    // power curve
    JFreeChart curve;
    // curve width/height
    int width;
    int height;
    
    public JFreeChart getCurve()
    {
        return curve;
    }
    
    public void setCurve(JFreeChart curve)
    {
        this.curve = curve;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }    
}
