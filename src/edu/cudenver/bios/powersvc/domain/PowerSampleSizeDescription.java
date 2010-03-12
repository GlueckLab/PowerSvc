package edu.cudenver.bios.powersvc.domain;

import edu.cudenver.bios.powersamplesize.parameters.PowerSampleSizeParameters;

public class PowerSampleSizeDescription
{
    // inputs to power or sample size calculation
    PowerSampleSizeParameters parameters = null;
    
    // power curve options
    PowerCurveDescription curveDescription = null;

    public PowerSampleSizeParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(PowerSampleSizeParameters parameters)
    {
        this.parameters = parameters;
    }

    public PowerCurveDescription getCurveDescription()
    {
        return curveDescription;
    }

    public void setCurveDescription(PowerCurveDescription curveDescription)
    {
        this.curveDescription = curveDescription;
    }
    
    
}
