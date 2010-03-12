package edu.cudenver.bios.powersvc.domain;

public class PowerCurveDescription
{
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    
    String title;
    String XAxisLabel;
    String YAxisLabel;
    
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
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getXAxisLabel()
    {
        return XAxisLabel;
    }
    
    public void setXAxisLabel(String xAxisLabel)
    {
        XAxisLabel = xAxisLabel;
    }
    
    public String getYAxisLabel()
    {
        return YAxisLabel;
    }
    
    public void setYAxisLabel(String yAxisLabel)
    {
        YAxisLabel = yAxisLabel;
    }
        
}
