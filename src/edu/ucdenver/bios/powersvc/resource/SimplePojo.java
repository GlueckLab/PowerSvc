package edu.ucdenver.bios.powersvc.resource;

import java.io.Serializable;

public class SimplePojo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -8930123102806787748L;
    
    String name;
    int number;
    double decimalNumber;
    
    public SimplePojo(String name, int number, double decimal)
    {
        this.name = name;
        this.number = number;
        this.decimalNumber = decimal;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public double getDecimalNumber()
    {
        return decimalNumber;
    }

    public void setDecimalNumber(double decimalNumber)
    {
        this.decimalNumber = decimalNumber;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Name=" + name + "&number=" + number + "&dec=" + decimalNumber);
        return buffer.toString();
    }
}
