package edu.cudenver.bios.powersvc.application;

import org.apache.log4j.Logger;

public class PowerLogger
{
    private static Logger instance = null;

    private PowerLogger() 
    {
    }

    public static Logger getInstance() 
    {
        if (instance == null) 
        {
            instance = Logger.getLogger("edu.cudenver.bios.powersvc.Power");
        }

        return instance;
    }
}

