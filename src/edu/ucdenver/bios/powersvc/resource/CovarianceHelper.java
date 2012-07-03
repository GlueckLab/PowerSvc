package edu.ucdenver.bios.powersvc.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;

public class CovarianceHelper {

    public static RealMatrix covarianceToRealMatrix(Covariance covariance, 
            RepeatedMeasuresNode rmNode) {
        if (covariance.getColumns() != covariance.getRows()) {
            throw new IllegalArgumentException("Non-square covariance matrix");
        }

        // convert the spacing list to integers
        ArrayList<Integer> intSpacingList = new ArrayList<Integer>();
        if (rmNode.getSpacingList() != null) {
            for(Spacing spacingValue: rmNode.getSpacingList()) {
                intSpacingList.add(spacingValue.getValue());
            }
        } else {
            for(int i = 0; i < rmNode.getNumberOfMeasurements(); i++) {
                intSpacingList.add(i);
            }
        }
        
        RealMatrix covarianceData = null;
        List<StandardDeviation> stddevList = covariance.getStandardDeviationList();

        if (stddevList != null && stddevList.size() > 0) {
            if (covariance.getRho() != Double.NaN && 
                    covariance.getDelta() != Double.NaN) {
                // indicates Lear correlation
                covarianceData = buildLearCovariance(covariance.getRows(), 
                        covariance.getColumns(), stddevList.get(0),
                        covariance.getRho(), covariance.getDelta(),
                        intSpacingList);
                double stddev = stddevList.get(0).getValue();
                covarianceData = covarianceData.scalarMultiply(stddev * stddev);

            } else if (stddevList.size() == covariance.getRows()) {
                // indicates structured correlation, so we convert to a covariance matrix
                covarianceData = new Array2DRowRealMatrix(covariance.getBlob().getData());
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
                                    stddevRow*stddevRow*stddevCol*stddevCol);
                        } 
                    }
                }
            }

        } else {
            // unstructured covariance
            covarianceData = new Array2DRowRealMatrix(covariance.getBlob().getData());
        }
        return covarianceData;

    }
    
    
    public static RealMatrix covarianceToRealMatrix(Covariance covariance, 
            List<ResponseNode> responsesList) {
        if (covariance.getColumns() != covariance.getRows()) {
            throw new IllegalArgumentException("Non-square covariance matrix");
        }

        // convert the spacing list to integers
        ArrayList<Integer> intSpacingList = new ArrayList<Integer>();
        if (rmNode.getSpacingList() != null) {
            for(Spacing spacingValue: rmNode.getSpacingList()) {
                intSpacingList.add(spacingValue.getValue());
            }
        } else {
            for(int i = 0; i < rmNode.getNumberOfMeasurements(); i++) {
                intSpacingList.add(i);
            }
        }
        
        RealMatrix covarianceData = null;
        List<StandardDeviation> stddevList = covariance.getStandardDeviationList();

        if (stddevList != null && stddevList.size() > 0) {
            if (covariance.getRho() != Double.NaN && 
                    covariance.getDelta() != Double.NaN) {
                // indicates Lear correlation
                covarianceData = buildLearCovariance(covariance.getRows(), 
                        covariance.getColumns(), stddevList.get(0),
                        covariance.getRho(), covariance.getDelta(),
                        intSpacingList);
                double stddev = stddevList.get(0).getValue();
                covarianceData = covarianceData.scalarMultiply(stddev * stddev);

            } else if (stddevList.size() == covariance.getRows()) {
                // indicates structured correlation, so we convert to a covariance matrix
                covarianceData = new Array2DRowRealMatrix(covariance.getBlob().getData());
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
                                    stddevRow*stddevRow*stddevCol*stddevCol);
                        } 
                    }
                }
            }

        } else {
            // unstructured covariance
            covarianceData = new Array2DRowRealMatrix(covariance.getBlob().getData());
        }
        return covarianceData;

    }
    
    
    /**
     * Create a Lear covariance matrix
     * @param rows row dimension
     * @param columns column dimension
     * @param stddev standard deviation
     * @param rho correlation for observations 1 unit apart
     * @param delta rate of correlation decay
     * @param spacingList spacing list
     * @return RealMatrix containing the Lear covariance
     */
    private static RealMatrix buildLearCovariance(int rows, int columns, StandardDeviation stddev,
            double rho, double delta, List<Integer> intSpacingList) {

        LearCorrelation lear = new LearCorrelation(intSpacingList);
        
        double[][] data = new double[rows][columns];
        
        for(int row = 0; row < rows; row++) {
            for(int col = row; col < columns; col++) {
                if (row == col) {
                    data[row][col] = stddev.getValue() * stddev.getValue();
                } else {
                    data[row][col] = lear.getRho(row, col, rho, delta);
                }
            }
        }
        
        return new Array2DRowRealMatrix(data);
    }
 
    
    
}
