package edu.cudenver.bios.powersvc.domain;

import org.jfree.chart.JFreeChart;

public class PowerResults
{
    /****** results ******/
    
    // calculated power value
    double power;
    // simulated power - must set simulated=true for a power simulation 
    // to be run
    double simulatedPower = -1;
    // power curve
    JFreeChart powerCurve;
    
    public double getPower()
    {
        return power;
    }

    public void setPower(double power)
    {
        this.power = power;
    }

    public double getSimulatedPower()
    {
        return simulatedPower;
    }

    public void setSimulatedPower(double simulatedPower)
    {
        this.simulatedPower = simulatedPower;
    }

    public JFreeChart getPowerCurve()
    {
        return powerCurve;
    }

    public void setPowerCurve(JFreeChart powerCurve)
    {
        this.powerCurve = powerCurve;
    }
}
