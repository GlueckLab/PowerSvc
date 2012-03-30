/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.powersvc.resource.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.restlet.resource.ClientResource;

import edu.ucdenver.bios.powersvc.resource.PowerResource;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

/**
 * Test class to connect to a local instance of the power service
 * 
 * @author Sarah Kreidler
 */
public class TestPowerResource extends TestCase
{
    // Create the client resource to connect to the power service
    ClientResource clientResource = null; 
    PowerResource powerResource = null;

    /**
     * Connect to the server
     */
    public void setUp()
    {
        try
        {
            clientResource = new ClientResource("http://localhost:8080/power/power"); 
            //clientResource = new ClientResource("http://sph-bi-sakhadeo:8080/power/power"); 
            powerResource = clientResource.wrap(PowerResource.class);
        }
        catch (Exception e)
        {
            System.err.println("Failed to connect to server: " + e.getMessage());
            fail();
        }
    }

    /**
     * Calculate power for a two-sample t test
     */
    public void testPower()
    {
        StudyDesign studyDesign = buildDesign();
        // calculate power
        try
        {
            ArrayList<PowerResult> powerList = powerResource.getPower(studyDesign);
            for(PowerResult power : powerList)
            {
                System.out.println(power.toXML());
            }
            assertTrue(true);
        }
        catch (Exception e)
        {
            System.err.println("testPower Failed to retrieve: " + e.getMessage());
            fail();
        }
    }

    /**
     * Create a StudyDesign object representing a basic t-test design
     * 
     * @return StudyDesign 
     */
    private StudyDesign buildDesign()
    {
        StudyDesign studyDesign = new StudyDesign();
        studyDesign.setName("Two Sample T-Test");

        //        // add a test 
        //
        //        // add tests
        //        params.addTest(Test.UNIREP);
        //        
        //        // add alpha values
        //        params.addAlpha(0.05);
        //
        //        // build beta matrix
        //        double [][] beta = {{0},{1}};
        //        params.setBeta(new FixedRandomMatrix(beta, null, false));
        //        // add beta scale values
        //        for(double betaScale = 0; betaScale <= 2.5; betaScale += 0.05) params.addBetaScale(betaScale);
        //        
        //        // build theta null matrix
        //        double [][] theta0 = {{0}};
        //        params.setTheta(new Array2DRowRealMatrix(theta0));
        //        
        //        // build sigma matrix
        //        double [][] sigma = {{1}};
        //        params.setSigmaError(new Array2DRowRealMatrix(sigma));
        //        // add sigma scale values
        //        for(double sigmaScale: SIGMA_SCALE_LIST) params.addSigmaScale(sigmaScale);
        //        
        //        // build design matrix
        //        params.setDesignEssence(MatrixUtils.createRealIdentityMatrix(2));
        //        // add sample size multipliers
        //        for(int sampleSize: SAMPLE_SIZE_LIST) params.addSampleSize(sampleSize);
        //        
        //        // build between subject contrast
        //        double [][] between = {{1,-1}};
        //        params.setBetweenSubjectContrast(new FixedRandomMatrix(between, null, true));


        return studyDesign;
    }
}
