package edu.cudenver.bios.powersvc.domain;

import edu.cudenver.bios.powersamplesize.parameters.PowerSampleSizeParameters;

public class SampleSizeInputs
{
    // inputs to the sample size calculation
    PowerSampleSizeParameters parameters;
    
    // if true, a power curve will be produced
    boolean showCurve = true;
    
    public PowerSampleSizeParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(PowerSampleSizeParameters parameters)
    {
        this.parameters = parameters;
    }

    public boolean hasCurve()
    {
        return showCurve;
    }

    public void setCurve(boolean showCurve)
    {
        this.showCurve = showCurve;
    }
    
}
