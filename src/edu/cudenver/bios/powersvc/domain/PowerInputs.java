package edu.cudenver.bios.powersvc.domain;

import edu.cudenver.bios.powersamplesize.parameters.PowerSampleSizeParameters;
import edu.cudenver.bios.powersvc.application.PowerConstants;

public class PowerInputs
{
    // inputs to power calculation or simulation
    PowerSampleSizeParameters parameters;
    
    // if true, a power simulation will be run (default is false)
    boolean simulated = false;
    int simulationIterations = PowerConstants.DEFAULT_SIMULATION_SIZE;
    
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

    public boolean isSimulated()
    {
        return simulated;
    }

    public void setSimulated(boolean simulated)
    {
        this.simulated = simulated;
    }

    public int getSimulationIterations()
    {
        return simulationIterations;
    }

    public void setSimulationIterations(int simulationIterations)
    {
        if (simulationIterations > PowerConstants.MAX_SIMULATION_SIZE)
            this.simulationIterations = PowerConstants.DEFAULT_SIMULATION_SIZE;
        else
            this.simulationIterations = simulationIterations;
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
