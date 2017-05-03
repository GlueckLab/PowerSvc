/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 *
 * Copyright (C) 2015 Regents of the University of Colorado.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import edu.cudenver.bios.matrix.MatrixUtilities;
import edu.cudenver.bios.matrix.MatrixUtils;
import edu.cudenver.bios.utils.Logger;
import edu.ucdenver.bios.powersvc.application.PowerLogger;
import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * Routines for generating covariance matrices from domain layer
 * covariance objects
 * @author Sarah Kreidler
 *
 */
public class CovarianceHelper {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(PowerLogger.getInstance());

    private static final String COVARIANCE_NOT_POSITIVE_SEMIDEFINITE_MESSAGE =
            "Unfortunately, there is no solution for this combination of input parameters. "
        +   "The \"@@NAME@@\" covariance matrix does not describe a valid "
        +   "covariance structure (that is, it is not positive semidefinite)."
        ;

    /**
     * Convert a covariance object into a RealMatrix.
     * @param covariance Covariance domain object
     * @param rmNode the repeated measures information associated
     * with the covariance object
     * @return covariance as a RealMatrix
     */
    public static RealMatrix covarianceToRealMatrix(Covariance covariance, RepeatedMeasuresNode rmNode) {
        return realMatrix(covariance, learSpacing(covariance, rmNode), rmNode.getDimension());
    }

    /**
     * Create a covariance matrix for responses
     * @param covariance the covariance domain object
     * @param responses the response variables
     * @return covariance matrix
     */
    public static RealMatrix covarianceToRealMatrix(Covariance covariance, List<ResponseNode> responses) {
        return realMatrix(covariance, learSpacing(covariance, responses), "Responses");
    }

    /**
     * Convert a correlation into a covariance matrix
     * @param covariance
     * @return covariance matrix
     */
    private static RealMatrix buildCovarianceFromCorrelation(Covariance covariance) {
        RealMatrix covarianceData = null;
        Blob2DArray blob = covariance.getBlob();

        if (blob != null && blob.getData() != null) {
            List<StandardDeviation> stddevList = covariance.getStandardDeviationList();
            if (stddevList.size() == covariance.getRows()) {
                covarianceData = new Array2DRowRealMatrix(covariance.getBlob().getData());
                /* For each diagonal cell, square the standard deviation
                 * For each off-diagonal cell, use formula
                 * covariance = correlation * sqrt (var1 * var2)
                 */
                for(int row = 0; row < covariance.getRows(); row++) {
                    for(int col = 0; col < covariance.getColumns(); col++) {
                        double value = covarianceData.getEntry(row, col);
                        if (row == col) {
                            double stddev = stddevList.get(row).getValue();
                            covarianceData.setEntry(row, col, stddev*stddev);
                        } else {
                            double stddevRow = stddevList.get(row).getValue();
                            double stddevCol = stddevList.get(col).getValue();
                            covarianceData.setEntry(row, col,
                                    value * Math.sqrt(stddevRow*stddevRow*stddevCol*stddevCol));
                        }
                    }
                }
            }
        }
        return covarianceData;
    }

    /**
     * Create a Lear covariance matrix
     * @param covariance Covariance domain object
     * @param learSpacing spacing list
     * @return RealMatrix containing the Lear covariance
     */
    private static RealMatrix buildLearCovariance(Covariance covariance, List<Integer> learSpacing) {
        RealMatrix covarianceData = null;
        int rows = covariance.getRows();
        int columns = covariance.getColumns();
        List<StandardDeviation> stddevList = covariance.getStandardDeviationList();
        // make sure everything is valid
        if (stddevList != null && stddevList.size() > 0 &&
                !Double.isNaN(covariance.getRho()) &&
                !Double.isNaN(covariance.getDelta()) &&
                covariance.getDelta() >= 0) {

            LearCorrelation lear = new LearCorrelation(learSpacing);
            StandardDeviation stddev = stddevList.get(0);

            double[][] data = new double[rows][columns];

            for(int row = 0; row < rows; row++) {
                for(int col = row; col < columns; col++) {
                    if (row == col) {
                        data[row][col] = stddev.getValue() * stddev.getValue();
                    } else {
                        double value = (lear.getRho(row, col, covariance.getRho(), covariance.getDelta()) *
                                stddev.getValue() * stddev.getValue());
                        data[row][col] = value;
                        data[col][row] = value;
                    }
                }
            }

            covarianceData =  new Array2DRowRealMatrix(data);
        }
        debug(
            "Server-side LEAR covariance matrix for '" + covariance.getName() + "' "
                + "(client-side LEAR correlation matrix is ignored):",
            covarianceData
        );
        return covarianceData;
    }

    /**
     * Create a covariance matrix, for either the repeated measures case
     * or the response variables case.
     *
     * @param covariance  The covariance domain object.
     * @param learSpacing The LEAR spacing, or null if the covariance type
     *                    is not LEAR.
     * @param name        In the repeated measures case, the 'dimension'
     *                    of the repeated measure; in the response
     *                    variables case, the literal string "Responses".
     *
     * @return The covariance matrix, or null if some input data was not
     *         as expected.
     */
    private static RealMatrix realMatrix(Covariance covariance, List<Integer> learSpacing, String name) {
        assert covariance != null;

        if (covariance.getColumns() != covariance.getRows()) {
            throw new IllegalArgumentException("Non-square covariance matrix.");
        }

        // create a covariance matrix based on the Covariance domain object
        RealMatrix covarianceData = null;

        if (covariance.getType() != null) {
            switch (covariance.getType()) {
            case LEAR_CORRELATION:
                /* LEAR correlation model.  Should have a standard deviation,
                 * rho, and delta specified */
                covarianceData = buildLearCovariance(covariance, learSpacing);
                break;
            case UNSTRUCTURED_CORRELATION:
                // indicates structured correlation, so we convert to a covariance matrix
                covarianceData = buildCovarianceFromCorrelation(covariance);
                break;
            case UNSTRUCTURED_COVARIANCE:
                // unstructured covariance, so simply extract the data
                Blob2DArray blob = covariance.getBlob();
                if (blob != null) {
                    covarianceData = new Array2DRowRealMatrix(covariance.getBlob().getData());
                }
                break;
            }
        }

        if (covarianceData != null) {
            if (! MatrixUtils.isPositiveSemidefinite(covarianceData)) {
                throw new IllegalArgumentException(COVARIANCE_NOT_POSITIVE_SEMIDEFINITE_MESSAGE.replace("@@NAME@@", name));
            }
        }

        return covarianceData;
    }

    /**
     * Compute the LEAR spacing for the repeated measures case.
     *
     * @param covariance The covariance domain object.
     * @param responses  The repeated measures.
     *
     * @return The LEAR spacing, or null if the covariance type
     *         is not LEAR.
     */
    private static List<Integer> learSpacing(Covariance covariance, RepeatedMeasuresNode rmNode) {
        if (covariance == null) {
            throw new IllegalArgumentException("No covariance matrix.");
        }

        if (covariance.getType() != CovarianceTypeEnum.LEAR_CORRELATION) {
            return null;
        }

        if (rmNode == null) {
            throw new IllegalArgumentException("No repeated measures.");
        }

        List<Integer> result = new ArrayList<Integer>();

        if (rmNode.getSpacingList() != null) {
            for (Spacing spacingValue: rmNode.getSpacingList()) {
                result.add(spacingValue.getValue());
            }
        } else {
            for (int i = 0, n = rmNode.getNumberOfMeasurements(); i < n; ++ i) {
                result.add(i);
            }
        }

        return result;
    }

    /**
     * Compute the LEAR spacing for the response variables case.
     *
     * @param covariance The covariance domain object.
     * @param responses  The response variables.
     *
     * @return The LEAR spacing, or null if the covariance type
     *         is not LEAR.
     */
    private static List<Integer> learSpacing(Covariance covariance, List<ResponseNode> responses) {
        if (covariance == null) {
            throw new IllegalArgumentException("No covariance matrix.");
        }

        if (covariance.getType() != CovarianceTypeEnum.LEAR_CORRELATION) {
            return null;
        }

        if (responses == null) {
            throw new IllegalArgumentException("No response variables.");
        }

        // create equal spacing across the responses
        List<Integer> result = new ArrayList<Integer>();

        for (int i = 0, n = responses.size(); i < n; ++ i) {
            result.add(i);
        }

        return result;
    }

    /**
     * A convenience method for DEBUG logging of a matrix
     * with a label.
     *
     * @param label      The label.
     * @param realMatrix The matrix.
     */
    private static void debug(String label, RealMatrix realMatrix) {
        LOGGER.debug(MatrixUtilities.logMessageSupplier(label, realMatrix));
    }
}
