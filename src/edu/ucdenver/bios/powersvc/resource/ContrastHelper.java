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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.powersvc.resource;

import java.util.List;

import org.apache.commons.math.linear.RealMatrix;

import edu.cudenver.bios.matrix.MatrixUtils;
import edu.cudenver.bios.matrix.OrthogonalPolynomials;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

/**
 * Contrast generator class
 * @author Sarah Kreidler
 */
public class ContrastHelper {
    /* TODO: this should be moved to the JavaStats library eventually */

    /**
     * 
     * @param factorOfInterest
     * @param factorList
     * @return
     */
    public static RealMatrix mainEffect(BetweenParticipantFactor factorOfInterest, 
            List<BetweenParticipantFactor> factorList) {

        // build contrast component for the effect of interest 
        int levels = factorOfInterest.getCategoryList().size();
        int df = levels-1;
        RealMatrix negIdentity = 
            org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(df).scalarMultiply(-1);
        RealMatrix column1s = MatrixUtils.getRealMatrixWithFilledValue(df, 1, 1);
        RealMatrix effectContrast = MatrixUtils.getHorizontalAppend(column1s, negIdentity);

        // perform a horizontal direct product across the factors with the effect of interest
        // and average contrasts for any remaining factors
        if (df > 0) {
            RealMatrix contrast = column1s;
            for(BetweenParticipantFactor factor: factorList) {
                if (factor.getPredictorName().equals(factorOfInterest.getPredictorName())) {
                    contrast = MatrixUtils.getHorizontalDirectProduct(contrast, effectContrast);
                } else {
                    List<Category> categoryList = factor.getCategoryList();
                    if (categoryList.size() > 0) {
                        int dimension = categoryList.size();
                        contrast = MatrixUtils.getKroneckerProduct(contrast,
                                MatrixUtils.getRealMatrixWithFilledValue(1, dimension, 1/(double) dimension));
                    }
                }
            }
            return contrast;
        } else {
            return ContrastHelper.grandMean(factorList);
        }
    }

    /**
     * 
     * @param betweenMap
     * @param factorList
     * @return
     */
    public static RealMatrix interaction(List<HypothesisBetweenParticipantMapping> betweenMap,
            List<BetweenParticipantFactor> factorList) {

        
        //        
        //        // perform a horizontal direct product across the factors with the effect of interest
        //        // and average contrasts for any remaining factors
        //            RealMatrix contrast = column1s;
        //            for(BetweenParticipantFactor factor: factorList) {
        //                if (factor.getPredictorName().equals(factorOfInterest.getPredictorName())) {
        //                    contrast = MatrixUtils.getHorizontalDirectProduct(contrast, effectContrast);
        //                } else {
        //                    List<Category> categoryList = factor.getCategoryList();
        //                    if (categoryList.size() > 0) {
        //                        int dimension = categoryList.size();
        //                        contrast = MatrixUtils.getHorizontalDirectProduct(contrast,
        //                                MatrixUtils.getRealMatrixWithFilledValue(df, dimension, 1/(double) dimension));
        //                    }
        //                }
        //            }
        //            return contrast;
        //        } else {
        return ContrastHelper.grandMean(factorList);
        //        }
    }

    /**
     * 
     * @param factorOfInterestMap
     * @param factorList
     * @return
     */
    public static RealMatrix trend(HypothesisBetweenParticipantMapping factorOfInterestMap, 
            List<BetweenParticipantFactor> factorList) {

        // build contrast component for the effect of interest 
        BetweenParticipantFactor factorOfInterest = factorOfInterestMap.getBetweenParticipantFactor();
        HypothesisTrendTypeEnum trendType = factorOfInterestMap.getType();
        int levels = factorOfInterest.getCategoryList().size();
        if (levels > 1) {
            // create even spacing
            double[] spacing = new double[levels];
            for(int i = 0; i < levels; i++) { spacing[i] = i; }
            RealMatrix trendContrast = getTrendContrast(spacing, trendType, true);

            // horizontal direct product the trend contrast with average contrasts for remaining
            // factors
            RealMatrix contrast = MatrixUtils.getRealMatrixWithFilledValue(levels, 1, 1);
            int df = trendContrast.getRowDimension();
            for(BetweenParticipantFactor factor: factorList) {
                if (factor.getPredictorName().equals(factorOfInterest.getPredictorName())) {
                    contrast = MatrixUtils.getHorizontalDirectProduct(contrast, trendContrast);
                } else {
                    List<Category> categoryList = factor.getCategoryList();
                    if (categoryList.size() > 0) {
                        int dimension = categoryList.size();
                        contrast = MatrixUtils.getHorizontalDirectProduct(contrast,
                                MatrixUtils.getRealMatrixWithFilledValue(df, dimension, 1/(double) dimension));
                    }
                }
            }
            return contrast;
        } else {
            return ContrastHelper.grandMean(factorList);
        }
    }

    /**
     * 
     * @param dimension
     * @param transpose
     * @return
     */
    public static RealMatrix grandMean(int dimension, boolean transpose) {
        if (transpose) {
            return MatrixUtils.getRealMatrixWithFilledValue(1, dimension, 1/(double) dimension).transpose();
        } else {
            return MatrixUtils.getRealMatrixWithFilledValue(1, dimension, 1/(double) dimension);
        }
    }

    /**
     * 
     * @param factorList
     * @return
     */
    public static RealMatrix grandMean(List<BetweenParticipantFactor> factorList) {
        // computes the grand mean across the factors
        int dimension = 1;
        for(BetweenParticipantFactor factor: factorList) {
            List<Category> categoryList = factor.getCategoryList();
            if (categoryList.size() > 0) {
                dimension *= categoryList.size();
            }
        }
        return MatrixUtils.getRealMatrixWithFilledValue(1, dimension, 1/(double) dimension);
    }
    
    /**
     * 
     * @param spacing
     * @param trendType
     * @param transpose
     * @return
     */
    private static RealMatrix getTrendContrast(double[] spacing, HypothesisTrendTypeEnum trendType, 
            boolean transpose) {
        int levels = spacing.length;
        // get all possible polynomial trends
        RealMatrix allTrendContrast = 
            OrthogonalPolynomials.orthogonalPolynomialCoefficients(spacing, 
                    Math.min(3,levels-1));
        // select a subset of the polynomial trends based on the hypothesis of interest
        RealMatrix trendContrast = null;
        switch (trendType) {
        case NONE:
            RealMatrix negIdentity = 
                org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(levels-1).scalarMultiply(-1);
            RealMatrix column1s = MatrixUtils.getRealMatrixWithFilledValue(levels-1, 1, 1);
            trendContrast = MatrixUtils.getHorizontalAppend(column1s, negIdentity).transpose();
            break;
        case CHANGE_FROM_BASELINE:
            trendContrast = MatrixUtils.getRealMatrixWithFilledValue(levels, 1, 0);
            trendContrast.setEntry(0, 0, -1);
            trendContrast.setEntry(levels-1, 0, -1);
            break;
        case ALL_POYNOMIAL:
            trendContrast = allTrendContrast;
            break;
        case LINEAR:
            if (allTrendContrast.getRowDimension() > 1) {
                trendContrast = allTrendContrast.getColumnMatrix(1);
            }
            break;
        case QUADRATIC:
            if (allTrendContrast.getRowDimension() > 2) {
                trendContrast = allTrendContrast.getColumnMatrix(2);
            }
            break;
        case CUBIC:
            if (allTrendContrast.getRowDimension() > 3) {
                trendContrast = allTrendContrast.getColumnMatrix(3);
            }
            break;
        }
        if (transpose) trendContrast = trendContrast.transpose();
        return trendContrast;
    }

}
