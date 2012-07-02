package edu.ucdenver.bios.powersvc.resource;

import java.util.List;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;

public class CovarianceHelper {

    public static RealMatrix covarianceToRealMatrix(Covariance covariance) {
        if (covariance.getColumns() != covariance.getRows()) {
            throw new IllegalArgumentException("Non-square covariance matrix");
        }
        Blob2DArray matrixDataBlob = covariance.getBlob();
        if (matrixDataBlob != null) {
            RealMatrix covarianceData = new Array2DRowRealMatrix(matrixDataBlob.getData());
            List<StandardDeviation> stddevList = covariance.getStandardDeviationList();
            if (stddevList != null && stddevList.size() > 0) {
                if (covariance.getRho() != Double.NaN && 
                        covariance.getDelta() != Double.NaN) {
                    // indicates Lear correlation
                    double stddev = stddevList.get(0).getValue();
                    covarianceData = covarianceData.scalarMultiply(stddev * stddev);
                } else if (stddevList.size() == covariance.getRows()) {
                    // indicates structured correlation, so we convert to a covariance matrix
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
            }
            return covarianceData;
        } else {
            return null;
        }
    }
}
