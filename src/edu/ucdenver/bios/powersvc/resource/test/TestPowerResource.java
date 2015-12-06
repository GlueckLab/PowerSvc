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

import edu.ucdenver.bios.powersvc.application.PowerConstants;
import edu.ucdenver.bios.powersvc.resource.PowerResource;
import edu.ucdenver.bios.powersvc.resource.PowerServerResource;
import edu.ucdenver.bios.powersvc.resource.SampleSizeResource;
import edu.ucdenver.bios.powersvc.resource.SampleSizeServerResource;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.NominalPower;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.SampleSize;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.StatisticalTestTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.StudyDesignViewTypeEnum;

/**
 * Test class to connect to a local instance of the power service
 *
 * @author Sarah Kreidler
 */
public class TestPowerResource extends TestCase
{
    // Create the client resources
    PowerResource powerResource = null;
    SampleSizeResource sampleSizeResource = null;

    /**
     * Connect to the server
     */
    public void setUp()
    {
        try
        {
            //clientResource = new ClientResource("http://sph-bi-sakhadeo:8080/power/power");
            powerResource = new PowerServerResource();
            sampleSizeResource = new SampleSizeServerResource();
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
     * Calculate samplesize for a one-sample t test
     */
    public void testSampleSize()
    {
        StudyDesign studyDesign = buildDesignOneSample();
        // calculate power
        try
        {
            ArrayList<PowerResult> powerList = sampleSizeResource.getSampleSize(studyDesign);
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

    /**
     * Create a StudyDesign object representing a basic t-test design
     *
     * @return StudyDesign
     */
    private StudyDesign buildDesignOneSample()
    {
        StudyDesign studyDesign = new StudyDesign();
        studyDesign.setViewTypeEnum(StudyDesignViewTypeEnum.GUIDED_MODE);
        studyDesign.setSolutionTypeEnum(SolutionTypeEnum.SAMPLE_SIZE);
        studyDesign.setName("One sample t test");

        ArrayList<BetaScale> betaScaleList = new ArrayList<BetaScale>();
        betaScaleList.add(new BetaScale(1));
        studyDesign.setBetaScaleList(betaScaleList);

        ArrayList<NominalPower> nominalPowerList = new ArrayList<NominalPower>();
        nominalPowerList.add(new NominalPower(0.9));
        studyDesign.setNominalPowerList(nominalPowerList);

        // add a test
        ArrayList<StatisticalTest> testList = new ArrayList<StatisticalTest>();
        testList.add(new StatisticalTest(StatisticalTestTypeEnum.HLT));
        studyDesign.setStatisticalTestList(testList);

        // add alpha values
        ArrayList<TypeIError> alphaList = new ArrayList<TypeIError>();
        alphaList.add(new TypeIError(0.05));
        studyDesign.setAlphaList(alphaList);

        // add sigma scale values
        ArrayList<SigmaScale> sigmaScaleList = new ArrayList<SigmaScale>();
        sigmaScaleList.add(new SigmaScale(1));
        studyDesign.setSigmaScaleList(sigmaScaleList);

        // build the hypotheses
        Hypothesis hypothesis = new Hypothesis();
//        hypothesis.setType(HypothesisTypeEnum.MAIN_EFFECT);
        hypothesis.setType(HypothesisTypeEnum.GRAND_MEAN);
        studyDesign.setHypothesisToSet(hypothesis);
        // set theta null
        double[][]  thetaNullData = {{0}};
        NamedMatrix thetaNull = new NamedMatrix(PowerConstants.MATRIX_THETA_NULL);
        thetaNull.setDataFromArray(thetaNullData);
        thetaNull.setRows(1);
        thetaNull.setColumns(1);
        studyDesign.setNamedMatrix(thetaNull);

        // build beta matrix
        double [][] betaData = {{2}};
        NamedMatrix beta = new NamedMatrix(PowerConstants.MATRIX_BETA);
        beta.setDataFromArray(betaData);
        beta.setRows(1);
        beta.setColumns(1);
        studyDesign.setNamedMatrix(beta);

        // build response variables list
        ArrayList<ResponseNode> responseList = new ArrayList<ResponseNode>();
        responseList.add(new ResponseNode("outcome"));
        studyDesign.setResponseList(responseList);

        // build covariance
        Covariance covar = new Covariance();
        covar.setType(CovarianceTypeEnum.UNSTRUCTURED_CORRELATION);
        covar.setName(PowerConstants.RESPONSES_COVARIANCE_LABEL);
        ArrayList<StandardDeviation> stdDevList = new ArrayList<StandardDeviation>();
        stdDevList.add(new StandardDeviation(3));
        double [][] sigmaData = {{1}};
        covar.setRows(1);
        covar.setColumns(1);
        covar.setBlobFromArray(sigmaData);
        covar.setStandardDeviationList(stdDevList);
        studyDesign.addCovariance(covar);
        return studyDesign;

    }
}
