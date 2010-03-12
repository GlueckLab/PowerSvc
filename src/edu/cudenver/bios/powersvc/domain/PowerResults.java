package edu.cudenver.bios.powersvc.domain;

public class PowerResults
{
    /****** results ******/
    
    // calculated power value
    double power;
    // simulated power - must set simulated=true for a power simulation 
    // to be run
    double simulatedPower = -1;

    // graphics results
    PowerCurveResults curveResults = null;
    
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

    public PowerCurveResults getCurveResults()
    {
        return curveResults;
    }

    public void setCurveResults(PowerCurveResults curveResults)
    {
        this.curveResults = curveResults;
    }

}
