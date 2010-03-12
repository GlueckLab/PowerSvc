package edu.cudenver.bios.powersvc.domain;

import edu.cudenver.bios.powersvc.application.PowerConstants;

public class PowerDescription extends PowerSampleSizeDescription
{
    // if true, a power simulation will be run (default is false)
    boolean simulated = false;
    int simulationIterations = PowerConstants.DEFAULT_SIMULATION_SIZE;
    
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
        this.simulationIterations = simulationIterations;
    }    
}
