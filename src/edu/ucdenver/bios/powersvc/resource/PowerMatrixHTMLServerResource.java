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

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.data.Form;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import edu.cudenver.bios.matrix.FixedRandomMatrix;
import edu.ucdenver.bios.powersvc.application.PowerConstants;
import edu.ucdenver.bios.powersvc.application.PowerLogger;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.StudyDesignViewTypeEnum;

/**
 * Resource which generates an HTML representation of the matrices
 * used in a power calculation
 * @author Sarah Kreidler
 *
 */
public class PowerMatrixHTMLServerResource extends ServerResource
implements PowerMatrixHTMLResource {
	
    private static final String MATH_OPEN = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">";
    private static final String MATH_CLOSE = "</math>";
    private static final String BOLD_OPEN = "<mstyle mathvariant=\"bold\" mathsize=\"normal\">";
    private static final String BOLD_CLOSE = "</mstyle>";
    private static final String MROW_OPEN = "<mrow>";
    private static final String MROW_CLOSE = "</mrow>";
    private static final String MI_OPEN = " <mi>";
    private static final String MI_CLOSE = "</mi> ";
    private static final String MO_OPEN = " <mo> ";
    private static final String MO_CLOSE = "</mo> ";
    private static final String MN_OPEN = " <mn>";
    private static final String MN_CLOSE = "</mn> ";
    private static final String MTABLE_OPEN = "<mtable cellspacing='4px'>";
    private static final String MTABLE_CLOSE = "</mtable>";
    private static final String MTR_OPEN = "<mtr>";
    private static final String MTR_CLOSE = "</mtr>";
    private static final String MSUB_OPEN = "<msub>";
    private static final String MSUB_CLOSE = "</msub>";
    private static final String MSUBSUP_OPEN = "<msubsup>";
    private static final String MSUBSUP_CLOSE = "</msubsup>";
    private static final String KRONECKER_PRODUCT = "&otimes;";
    private static final DecimalFormat formatter = new DecimalFormat("0.0000");
    
    /**
     * Get matrices used in the power calculation for a "guided" study design
     * as an HTML formatted string.  This method uses the notation of
     * Muller & Stewart 2007
     * @param studyDesign the StudyDesign object
     * @return html string with representation of matrices
     */
    @Post("json:html")
    public String getMatricesAsHTML(StudyDesign studyDesign) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<html><head><script type=\"text/javascript\" " +
        		"src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?" +
        		"config=TeX-AMS-MML_HTMLorMML\"></script></head><body>");
        if (studyDesign != null) {
            // calculate cluster size
            List<ClusterNode> clusterNodeList = studyDesign.getClusteringTree();
            int clusterSize = 1;
            if (clusterNodeList != null && clusterNodeList.size() > 0) {
                for(ClusterNode node: clusterNodeList) {
                    clusterSize *= node.getGroupSize();
                }
            }

            /*
             * We are clearing and resetting the clustering information
             * here for the sake of reusing the functions in PowerResourceHelper.
             * We need to get the beta, C, U, and theta null matrices WITHOUT
             * the clustering adjustment for the purposes of display.
             */
            studyDesign.setClusteringTree(null);
            FixedRandomMatrix B = 
                    PowerResourceHelper.betaMatrixFromStudyDesign(studyDesign);
            FixedRandomMatrix C = 
                    PowerResourceHelper.betweenParticipantContrastFromStudyDesign(studyDesign);
            RealMatrix U = 
                    PowerResourceHelper.withinParticipantContrastFromStudyDesign(studyDesign);
            RealMatrix thetaNull = 
                    PowerResourceHelper.thetaNullMatrixFromStudyDesign(studyDesign, C, U);
            
            // add MathML code for the matrices
            // design matrix
            buffer.append(matrixHeaderMathML(PowerConstants.DISPLAY_MATRIX_DESIGN));
            buffer.append(realMatrixToMathML(
                    PowerResourceHelper.designMatrixFromStudyDesign(
                            studyDesign), false));
            buffer.append(matrixCloseMathML());
            // beta matrix
            buffer.append(matrixHeaderMathML(PowerConstants.DISPLAY_MATRIX_BETA));
            if (clusterSize > 1) {
                buffer.append(getColumnOfOnesMathML(clusterSize, true));
                buffer.append(MO_OPEN);
                buffer.append(KRONECKER_PRODUCT);
                buffer.append(MO_CLOSE);   
            }
            buffer.append(fixedRandomMatrixToMathML(B));
            buffer.append(matrixCloseMathML());
            // between participant contrast
            buffer.append(matrixHeaderMathML(PowerConstants.DISPLAY_MATRIX_BETWEEN_CONTRAST));
            buffer.append(fixedRandomMatrixToMathML(C));
            buffer.append(matrixCloseMathML());
            // within participant contrast
            buffer.append(matrixHeaderMathML(
                    PowerConstants.DISPLAY_MATRIX_WITHIN_CONTRAST));
            if (clusterSize > 1) {
                buffer.append(getColumnOfOnesMathML(clusterSize, false));
                buffer.append(MO_OPEN);
                buffer.append(KRONECKER_PRODUCT);
                buffer.append(MO_CLOSE);   
            }
            buffer.append(realMatrixToMathML(U, false));
            buffer.append(matrixCloseMathML());
            // theta null matrix
            buffer.append(matrixHeaderMathML(
                    PowerConstants.DISPLAY_MATRIX_THETA_NULL));
            buffer.append(realMatrixToMathML(thetaNull, false));
            buffer.append(matrixCloseMathML());
            
            // add matrices for either GLMM(F) or GLMM(F,g) designs
            if (studyDesign.isGaussianCovariate()) {
                RealMatrix sigmaY = 
                    PowerResourceHelper.sigmaOutcomesMatrixFromStudyDesign(studyDesign);
                RealMatrix sigmaG = 
                    PowerResourceHelper.sigmaCovariateMatrixFromStudyDesign(studyDesign);
                RealMatrix sigmaYG = 
                        PowerResourceHelper.sigmaOutcomesCovariateMatrixFromStudyDesign(
                                studyDesign, sigmaG, sigmaY);

                // sigma for Gaussian covariate
                buffer.append(matrixHeaderMathML(
                        PowerConstants.DISPLAY_MATRIX_SIGMA_GAUSSIAN));
                buffer.append(realMatrixToMathML(sigmaG, false));
                buffer.append(matrixCloseMathML());
                // sigma for Gaussian covariate and outcomes
                buffer.append(matrixHeaderMathML(
                        PowerConstants.DISPLAY_MATRIX_SIGMA_OUTCOME_GAUSSIAN));
                if (clusterSize > 1) {
                    buffer.append(getColumnOfOnesMathML(clusterSize, false));
                    buffer.append(MO_OPEN);
                    buffer.append(KRONECKER_PRODUCT);
                    buffer.append(MO_CLOSE);   
                }
                buffer.append(realMatrixToMathML(sigmaYG, false));
                buffer.append(matrixCloseMathML());
                // sigma outcomes matrix
                studyDesign.setClusteringTree(clusterNodeList);
                buffer.append(getSigmaOutcomeMatrixMathML(studyDesign));

            } else {
                // sigma error
                studyDesign.setClusteringTree(clusterNodeList);
                buffer.append(getSigmaErrorMatrixMathML(studyDesign));
            }
            
            buffer.append("<p/>For notation details, please see<p/>");
            buffer.append(createCitations());
            buffer.append(createBrowserNotes());
        } else {
            buffer.append("No study design specified");
        }

        buffer.append("</body></html>");
        return buffer.toString();
    }
    
    /**
     * Get matrices used in the power calculation for a "guided" study design
     * as an HTML formatted string.  This method required HTML form input
     * with the study design json in the 'studydesign' field. This method uses the notation of
     * Muller & Stewart 2007.
     */
    @Post("form:html")
    public String getMatricesAsHTML(Form studyDesignForm) {
        StudyDesign studyDesign = getStudyDesignFromForm(studyDesignForm);
        if (studyDesign != null) {
            return getMatricesAsHTML(studyDesign);
        } else {
            return "No study design specified";
        }
    }
    
    private StudyDesign getStudyDesignFromForm(Form studyDesignForm) {
        String jsonStudyDesign = studyDesignForm.getFirstValue("studydesign");
        if (jsonStudyDesign != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                StudyDesign design = mapper.readValue(jsonStudyDesign, StudyDesign.class);
                return design;
            } catch (Exception e) {
                PowerLogger.getInstance().error("Invalid study design: " + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * Create a MathML string with the matrix start block
     * @param name name of the matrix
     * @return MathML matrix start block
     */
    private String matrixHeaderMathML(String name) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(MATH_OPEN);
        buffer.append(MROW_OPEN);
        buffer.append(name);
        buffer.append(MO_OPEN);
        buffer.append(" = ");
        buffer.append(MO_CLOSE); 
        return buffer.toString();
    }
    
    /**
     * Create a MathML string with the matrix close block
     * @return MathML matrix close block
     */
    private String matrixCloseMathML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(MROW_CLOSE);
        buffer.append(MATH_CLOSE);
        buffer.append("<br/>");
        return buffer.toString();
    }
    
    /**
     * Create a matrix representing with a column of 1's using 
     * Muller & Stewart 2007 notation.
     * 
     * @param size column size
     * @param transpose indicates if the matrix is transposed.
     * @return string representation of matrix
     */
    private String getColumnOfOnesMathML(int size, boolean transpose) {
        StringBuffer buffer = new StringBuffer();

        if (transpose) {
            buffer.append(MSUBSUP_OPEN);
            buffer.append(BOLD_OPEN);
            buffer.append(MN_OPEN);
            buffer.append("1");
            buffer.append(MN_CLOSE);
            buffer.append(BOLD_CLOSE);
            buffer.append(MN_OPEN);
            buffer.append(Integer.toString(size));
            buffer.append(MN_CLOSE);
            buffer.append(MO_OPEN);
            buffer.append("'");
            buffer.append(MO_CLOSE);
            buffer.append(MSUBSUP_CLOSE);
        } else {
            buffer.append(MSUB_OPEN);
            buffer.append(BOLD_OPEN);
            buffer.append(MN_OPEN);
            buffer.append("1");
            buffer.append(MN_CLOSE);
            buffer.append(BOLD_CLOSE);
            buffer.append(MN_OPEN);
            buffer.append(Integer.toString(size));
            buffer.append(MN_CLOSE);
            buffer.append(MSUB_CLOSE);
        }
        return buffer.toString();
    }
    
    /**
     * Create mathML for an identity matrix using
     * Muller & Stewart 2007 notation.
     * 
     * @param size column size
     * @param transpose indicates if the matrix is transposed.
     * @return string representation of matrix
     */
    private String getIdentityMathML(int size, boolean transpose) {
        StringBuffer buffer = new StringBuffer();

        if (transpose) {
            buffer.append(MSUBSUP_OPEN);
            buffer.append(BOLD_OPEN);
            buffer.append(MI_OPEN);
            buffer.append("I");
            buffer.append(MI_CLOSE);
            buffer.append(BOLD_CLOSE);
            buffer.append(MN_OPEN);
            buffer.append(Integer.toString(size));
            buffer.append(MN_CLOSE);
            buffer.append(MO_OPEN);
            buffer.append("'");
            buffer.append(MO_CLOSE);
            buffer.append(MSUBSUP_CLOSE);
        } else {
            buffer.append(MSUB_OPEN);
            buffer.append(BOLD_OPEN);
            buffer.append(MI_OPEN);
            buffer.append("I");
            buffer.append(MI_CLOSE);
            buffer.append(BOLD_CLOSE);
            buffer.append(MN_OPEN);
            buffer.append(Integer.toString(size));
            buffer.append(MN_CLOSE);
            buffer.append(MSUB_CLOSE);
        }
        return buffer.toString();
    }
    
    /**
     * Create mathML for the sigma error matrix
     * @param studyDesign
     * @return mathML block
     */
    private String getSigmaMatrixMathML(String name, StudyDesign studyDesign) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(MATH_OPEN);
        buffer.append(MROW_OPEN);
        buffer.append(name);
        buffer.append(MO_OPEN);
        buffer.append(" = ");
        buffer.append(MO_CLOSE);  
        
        // add covariance information for clustering
        boolean first = true;
        List<ClusterNode> clusterNodeList = studyDesign.getClusteringTree();
        if (clusterNodeList != null) {
            for(ClusterNode clusterNode: clusterNodeList) {
                if (!first) {
                    buffer.append(MO_OPEN);
                    buffer.append(KRONECKER_PRODUCT);
                    buffer.append(MO_CLOSE);   
                }
                int size = clusterNode.getGroupSize();
                double rho = clusterNode.getIntraClusterCorrelation();
                buffer.append(getCompoundSymmetricMathML(size, rho));
                if (first) {
                    first = false;
                }
            }
        }
        
        // add covariance for repeated measures
        List<RepeatedMeasuresNode> rmNodeList = studyDesign.getRepeatedMeasuresTree();
        if (rmNodeList != null) {
            for(RepeatedMeasuresNode rmNode: rmNodeList) {
                Covariance covariance = studyDesign.getCovarianceFromSet(rmNode.getDimension());
                if (covariance != null) {
                    RealMatrix kroneckerMatrix = 
                            CovarianceHelper.covarianceToRealMatrix(covariance, rmNode);
                    if (kroneckerMatrix != null) {
                        if (!first) {
                            buffer.append(MO_OPEN);
                            buffer.append(KRONECKER_PRODUCT);
                            buffer.append(MO_CLOSE);   
                        }
                        buffer.append(realMatrixToMathML(kroneckerMatrix, false));
                        if (first) {
                            first = false;
                        }
                    }
                }
            }
        }
        // lastly, we need to add the covariance of responses
        Covariance covariance = studyDesign.getCovarianceFromSet(
                PowerConstants.RESPONSES_COVARIANCE_LABEL);
        RealMatrix kroneckerMatrix = CovarianceHelper.covarianceToRealMatrix(covariance, 
                studyDesign.getResponseList());
        if (kroneckerMatrix != null) {
            if (!first) {
                buffer.append(MO_OPEN);
                buffer.append(KRONECKER_PRODUCT);
                buffer.append(MO_CLOSE);   
            }
            buffer.append(realMatrixToMathML(kroneckerMatrix, false));
        } 
        
        buffer.append(MROW_CLOSE);
        buffer.append(MATH_CLOSE + "<br/>");
        
        return buffer.toString();
    }    
    
    /**
     * Get mathML for a compound symmetric correlation matrix
     * @param size size of the matrix
     * @param rho correlation parameter
     * @return mathML for matrix
     */
    private String getCompoundSymmetricMathML(int size, double rho) {
        // append a compound symmetric correlation matrix
        StringBuffer buffer = new StringBuffer();
        buffer.append(MROW_OPEN);
        buffer.append(MO_OPEN);
        buffer.append("[");
        buffer.append(MO_CLOSE);
        buffer.append(getColumnOfOnesMathML(size,false));
        buffer.append(getColumnOfOnesMathML(size,true));
        buffer.append(MO_OPEN);
        buffer.append("(");
        buffer.append(MO_CLOSE);
        buffer.append(MN_OPEN);
        buffer.append(formatter.format(rho));
        buffer.append(MN_CLOSE);
        buffer.append(MO_OPEN);
        buffer.append(")");
        buffer.append(MO_CLOSE);
        buffer.append(MO_OPEN);
        buffer.append(" + ");
        buffer.append(MO_CLOSE);
        buffer.append(getIdentityMathML(size, false));
        buffer.append(MO_OPEN);
        buffer.append("(");
        buffer.append(MO_CLOSE);
        buffer.append(MN_OPEN);
        buffer.append(1);
        buffer.append(MN_CLOSE);
        buffer.append(MO_OPEN);
        buffer.append(" - ");
        buffer.append(MO_CLOSE);
        buffer.append(MN_OPEN);
        buffer.append(formatter.format(rho));
        buffer.append(MN_CLOSE);
        buffer.append(MO_OPEN);
        buffer.append(")");
        buffer.append(MO_CLOSE);
        buffer.append(MO_OPEN);
        buffer.append("]");
        buffer.append(MO_CLOSE);
        
        return buffer.toString();
    }
    
    /**
     * Create mathML for the sigma error matrix
     * @param studyDesign
     * @return mathML block
     */
    private String getSigmaErrorMatrixMathML(StudyDesign studyDesign) {
        if (studyDesign.getViewTypeEnum() == StudyDesignViewTypeEnum.MATRIX_MODE) {
            return realMatrixDisplayToMathML(
                    PowerConstants.DISPLAY_MATRIX_SIGMA_ERROR,
                    PowerResourceHelper.toRealMatrix(
                            studyDesign.getNamedMatrix(
                                    PowerConstants.MATRIX_SIGMA_ERROR)),
                            1, false);
        } else {
            return getSigmaMatrixMathML(
                    PowerConstants.DISPLAY_MATRIX_SIGMA_ERROR,
                    studyDesign);
        }
    }  
    
    /**
     * Create mathML for the sigma error matrix
     * @param studyDesign
     * @return mathML block
     */
    private String getSigmaOutcomeMatrixMathML(StudyDesign studyDesign) {
        if (studyDesign.getViewTypeEnum() == StudyDesignViewTypeEnum.MATRIX_MODE) {
            return realMatrixDisplayToMathML(
                    PowerConstants.DISPLAY_MATRIX_SIGMA_OUTCOME,
                    PowerResourceHelper.toRealMatrix(
                            studyDesign.getNamedMatrix(
                                    PowerConstants.MATRIX_SIGMA_OUTCOME)),
                            1, false);
        } else {
            return getSigmaMatrixMathML(
                    PowerConstants.DISPLAY_MATRIX_SIGMA_OUTCOME,
                    studyDesign);
        }
    }    
    
    /**
     * Create a MathML representation of the matrix
     * @param name
     * @param matrix
     * @param clusterSize
     * @param transpose
     * @return
     */
    private String realMatrixDisplayToMathML(String name, RealMatrix baseMatrix,
            int clusterSize, boolean transpose) {
        StringBuffer buffer = new StringBuffer();
        if (baseMatrix != null) {
            buffer.append(MATH_OPEN);
            buffer.append(MROW_OPEN);
            buffer.append(name);
            buffer.append(MO_OPEN);
            buffer.append(" = ");
            buffer.append(MO_CLOSE);  
            RealMatrix matrix = baseMatrix;
            if (transpose) {
                buffer.append(MROW_OPEN);
                buffer.append(MO_OPEN);
                buffer.append("(");
                buffer.append(MO_CLOSE);  
                matrix = baseMatrix.transpose();
            }
            
            int rows = matrix.getRowDimension();
            int columns = matrix.getColumnDimension();

            buffer.append(MROW_OPEN);
            buffer.append(MO_OPEN);
            buffer.append("[");
            buffer.append(MO_CLOSE);        
            buffer.append(MTABLE_OPEN);   
            
            for(int row = 0; row < rows; row++) {
                buffer.append(MTR_OPEN);   
                for(int col = 0; col < columns; col++) {
                    buffer.append(MN_OPEN);
                    buffer.append(formatter.format(matrix.getEntry(row, col)));
                    buffer.append(MN_CLOSE);    
                }
                buffer.append(MTR_CLOSE);   
            }
            buffer.append(MTABLE_CLOSE);   
            buffer.append(MO_OPEN);
            buffer.append("]");
            buffer.append(MO_CLOSE);   

            if (transpose) {
                buffer.append(MO_OPEN);
                buffer.append("'");
                buffer.append(MO_CLOSE);   
            }
            buffer.append(MROW_CLOSE);
            
            if (clusterSize > 1) {
                buffer.append(MO_OPEN);
                buffer.append(KRONECKER_PRODUCT);
                buffer.append(MO_CLOSE);   

                buffer.append(getColumnOfOnesMathML(clusterSize, transpose));
            }
            
            
            if (transpose) {
                buffer.append(MO_OPEN);
                buffer.append(")");
                buffer.append(MO_CLOSE);  
                buffer.append(MO_OPEN);
                buffer.append("'");
                buffer.append(MO_CLOSE);   
                buffer.append(MROW_CLOSE);
            }
            
            buffer.append(MROW_CLOSE);
            buffer.append(MATH_CLOSE + "<br/>");
        }
        

        return buffer.toString();
    }
    
    /**
     * Create a MathML representation of the matrix
     * @param name
     * @param matrix
     * @param clusterSize
     * @param transpose
     * @return
     */
    private String realMatrixToMathML(RealMatrix baseMatrix, boolean transpose) {
        StringBuffer buffer = new StringBuffer();
        if (baseMatrix != null) {
            RealMatrix matrix = baseMatrix;
            if (transpose) {
                matrix = baseMatrix.transpose();
            }
            
            int rows = matrix.getRowDimension();
            int columns = matrix.getColumnDimension();

            buffer.append(MO_OPEN);
            buffer.append("[");
            buffer.append(MO_CLOSE);        
            buffer.append(MTABLE_OPEN);   
            
            for(int row = 0; row < rows; row++) {
                buffer.append(MTR_OPEN);   
                for(int col = 0; col < columns; col++) {
                    buffer.append(MN_OPEN);
                    buffer.append(formatter.format(matrix.getEntry(row, col)));
                    buffer.append(MN_CLOSE);    
                }
                buffer.append(MTR_CLOSE);   
            }
            buffer.append(MTABLE_CLOSE);   
            buffer.append(MO_OPEN);
            buffer.append("]");
            buffer.append(MO_CLOSE);   

            if (transpose) {
                buffer.append(MO_OPEN);
                buffer.append("'");
                buffer.append(MO_CLOSE);   
            }
        }
        

        return buffer.toString();
    }
    
    /**
     * Convert a fixed random matrix to mathML
     * @param name
     * @param matrix
     * @param clusterSize
     * @return
     */
    private String fixedRandomMatrixToMathML(FixedRandomMatrix matrix) {
        RealMatrix completeMatrix = matrix.getCombinedMatrix();
        return realMatrixToMathML(completeMatrix, false);
    }
    
    /**
     * Output the notation citations
     * @return HTML citation block
     */
    private String createCitations() {
        return "<div class=\"csl-bib-body\" style=\"line-height: 1.35; \">" +
        		"<div class=\"csl-entry\" style=\"margin-bottom: 1em;\">" +
        		"1. Glueck DH, Muller KE. Adjusting power for a baseline " +
        		"covariate in linear models. <i>Statistics in Medicine</i>. " +
        		"2003;22:2535-2551.</div><span class=\"Z3988\" title=" +
        		"\"url_ver=Z39.88-2004&amp;ctx_ver=Z39.88-2004&amp;" +
        		"rfr_id=info%3Asid%2Fzotero.org%3A2&amp;rft_val_fmt=" +
        		"info%3Aofi%2Ffmt%3Akev%3Amtx%3Ajournal&amp;rft.genre" +
        		"=article&amp;rft.atitle=Adjusting%20power%20for%20a%20" +
        		"baseline%20covariate%20in%20linear%20models&amp;rft.jtitle=" +
        		"Statistics%20in%20Medicine&amp;rft.volume=22&amp;rft.aufirst" +
        		"=D.%20H&amp;rft.aulast=Glueck&amp;rft.au=D.%20H%20Glueck" +
        		"&amp;rft.au=K.%20E%20Muller&amp;rft.date=2003&amp;rft.pages" +
        		"=2535-2551&amp;rft.spage=2535&amp;rft.epage=2551\"/>" +
        		"<div class=\"csl-entry\">2. Muller KE, Stewart PW. <i>Linear Model " +
        		"Theory: Univariate, Multivariate, and Mixed Models</i>. Hoboken, NJ: " +
        		"Wiley; 2006.</div><span class=\"Z3988\" title=\"url_ver=Z39.88-2004" +
        		"&amp;ctx_ver=Z39.88-2004&amp;rfr_id=info%3Asid%2Fzotero.org%3A2" +
        		"&amp;rft_val_fmt=info%3Aofi%2Ffmt%3Akev%3Amtx%3Abook&amp;" +
        		"rft.genre=book&amp;rft.btitle=Linear%20Model%20Theory%3A%20" +
        		"Univariate%2C%20Multivariate%2C%20and%20Mixed%20Models" +
        		"&amp;rft.place=Hoboken%2C%20NJ&amp;rft.publisher=Wiley" +
        		"&amp;rft.aufirst=Keith%20E&amp;rft.aulast=Muller&amp;" +
        		"rft.au=Keith%20E%20Muller&amp;rft.au=Paul%20W%20Stewart" +
        		"&amp;rft.date=2006\"/></div>";
    }
    
    private String createBrowserNotes() {
        return "<p/>This feature requires browser support of MathML.  Please see " +
        		"<a href=\"http://en.wikipedia.org/wiki/MathML\">Wikipedia's MathML " +
        		"Page</a> for information regarding supported browsers.";
    }
}
