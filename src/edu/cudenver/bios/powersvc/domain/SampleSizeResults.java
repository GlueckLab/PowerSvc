package edu.cudenver.bios.powersvc.domain;

public class SampleSizeResults
{
    // estimated sample size 
    int sampleSize;
    // actual power associated with sample size above - may not match the 
    // desired power exactly due to rounding errors and limitations with sample
    // size calculations for more complex models
    double actualPower;
    
    public int getSampleSize()
    {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize)
    {
        this.sampleSize = sampleSize;
    }

    public double getActualPower()
    {
        return actualPower;
    }

    public void setActualPower(double actualPower)
    {
        this.actualPower = actualPower;
    }
    
}
