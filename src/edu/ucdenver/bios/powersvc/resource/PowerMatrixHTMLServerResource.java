/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 *
 * Copyright (C) 2017 Regents of the University of Colorado.
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
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
 * Implementation of the PowerMatrixHTMLResource interface
 * for calculating an HTML/MathJax representation of the matrices
 * used in a power calculation.
 *
 * @author Sarah Kreidler
 */
public class PowerMatrixHTMLServerResource extends ServerResource
        implements PowerMatrixHTMLResource {
    private Logger logger = Logger.getLogger(getClass());

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // display names for matrices (used in MatrixHTML resource)
    public static final String DISPLAY_MATRIX_BETA = "\\boldsymbol{B}";
    public static final String DISPLAY_MATRIX_DESIGN = "\\text{Es}\\left(\\boldsymbol{X}\\right)";
    public static final String DISPLAY_MATRIX_THETA_NULL = "\\boldsymbol{\\Theta}_{0}";
    public static final String DISPLAY_MATRIX_THETA_OBSERVED = "\\boldsymbol{\\Theta}";

    public static final String DISPLAY_MATRIX_WITHIN_CONTRAST = "\\boldsymbol{U}";
    public static final String DISPLAY_MATRIX_BETWEEN_CONTRAST = "\\boldsymbol{C}";
    public static final String DISPLAY_MATRIX_SIGMA_ERROR = "\\boldsymbol{\\Sigma}_{E}";
    public static final String DISPLAY_MATRIX_SIGMA_GAUSSIAN = "\\boldsymbol{\\Sigma}_{g}";
    public static final String DISPLAY_MATRIX_SIGMA_OUTCOME = "\\boldsymbol{\\Sigma}_{Y}";
    public static final String DISPLAY_MATRIX_SIGMA_OUTCOME_GAUSSIAN = "\\boldsymbol{\\Sigma}_{Yg}";

    private static final String KRONECKER_PRODUCT = "\\otimes";
    private static final String LATEX_MATRIX_BEGIN = "\\begin{bmatrix}";
    private static final String LATEX_MATRIX_END = "\\end{bmatrix}";

    private static final DecimalFormat formatter = new DecimalFormat("0.0000");

    /**
     * Get matrices used in the power calculation for a "guided" study design
     * as an HTML formatted string.
     * <p>
     * This method uses the notation of Muller & Stewart 2007.
     *
     * @param jsonStudyDesign study design JSON
     *
     * @return html string with representation of matrices
     */
    @Post("json:html")
    public String getMatricesAsHTML(String jsonStudyDesign) {
        if (jsonStudyDesign == null) {
            throw badRequestException("Invalid study design.");
        }

        logger.info("getMatricesAsHTML(): " + getRequest().getRootRef() + ": "
                        + "jsonStudyDesign = '" + jsonStudyDesign + "'");

        StudyDesign studyDesign;

        try {
            studyDesign = MAPPER.readValue(jsonStudyDesign, StudyDesign.class);
        } catch (IOException ioe) {
            PowerLogger.getInstance().error(ioe.getMessage(), ioe);
            throw badRequestException(ioe.getMessage());
        }

        String result = privateGetMatricesAsHTML(studyDesign);

        logger.info("INPUT = '" + jsonStudyDesign + "'");
        logger.info("OUTPUT = '" + result + "'");

        return result;
    }

    /**
     * Get matrices used in the power calculation for a "guided" study design
     * as an HTML formatted string.
     * This is only called by test code.
     * <p>
     * This method uses the notation of Muller & Stewart 2007.
     *
     * @param studyDesign study design object
     *
     * @return html string with representation of matrices
     */
    public String getMatricesAsHTML(StudyDesign studyDesign) {
        return privateGetMatricesAsHTML(studyDesign);
    }

    /**
     * Get matrices used in the power calculation for a "guided" study design
     * as an HTML formatted string.
     * <p>
     * This method uses the notation of Muller & Stewart 2007.
     *
     * @param studyDesign study design object
     *
     * @return html string with representation of matrices
     */
    private String privateGetMatricesAsHTML(StudyDesign studyDesign) {
        if (studyDesign == null) {
            throw badRequestException("Invalid study design.");
        }

        StringBuilder buffer = new StringBuilder();

        buffer.append("<html><head><script type=\"text/javascript\" ")
              .append("src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?")
              .append("config=TeX-AMS-MML_HTMLorMML\"></script></head><body>")
              .append("<script>MathJax.Hub.Queue([\"Typeset\",MathJax.Hub]);</script>");
        if (studyDesign != null) {
            // calculate cluster size
            List<ClusterNode> clusterNodeList = studyDesign.getClusteringTree();
            int clusterSize = 1;
            if (clusterNodeList != null && clusterNodeList.size() > 0) {
                for(ClusterNode node: clusterNodeList) {
                    clusterSize *= node.getGroupSize();
                }
            }

            FixedRandomMatrix B = PowerResourceHelper.betaMatrixFromStudyDesign(studyDesign);
            FixedRandomMatrix C = PowerResourceHelper.betweenParticipantContrastFromStudyDesign(studyDesign);
            RealMatrix U = PowerResourceHelper.withinParticipantContrastFromStudyDesign(studyDesign);
            RealMatrix thetaObserved = C.getCombinedMatrix().multiply(B.getCombinedMatrix().multiply(U));

            /*
             * We are clearing and resetting the clustering information
             * here for the sake of reusing the functions in PowerResourceHelper.
             * We need to get several matrices WITHOUT the clustering adjustment
             * for the purposes of display.
             */
            studyDesign.setClusteringTree(null);
            B = PowerResourceHelper.betaMatrixFromStudyDesign(studyDesign);
            C = PowerResourceHelper.betweenParticipantContrastFromStudyDesign(studyDesign);
            U = PowerResourceHelper.withinParticipantContrastFromStudyDesign(studyDesign);
            RealMatrix thetaNull = PowerResourceHelper.thetaNullMatrixFromStudyDesign(studyDesign, C, U);

            if (studyDesign.isGaussianCovariate()) {
                RealMatrix sigmaY =
                    PowerResourceHelper.sigmaOutcomesMatrixFromStudyDesign(studyDesign);
                RealMatrix sigmaG =
                    PowerResourceHelper.sigmaCovariateMatrixFromStudyDesign(studyDesign);
                RealMatrix sigmaYG =
                        PowerResourceHelper.sigmaOutcomesCovariateMatrixFromStudyDesign(
                                studyDesign, sigmaG, sigmaY);
                RealMatrix sigmaGY = sigmaYG.transpose();
                RealMatrix sigmaGInverse = new LUDecomposition(sigmaG).getSolver().getInverse();
                B.updateRandomMatrix(sigmaGInverse.multiply(sigmaGY));
            }

            // add MathJax code for the matrices
            // design matrix
            buffer.append(getBeginEquation());
            buffer.append(realMatrixToTex(DISPLAY_MATRIX_DESIGN,
                    PowerResourceHelper.designMatrixFromStudyDesign(studyDesign),
                    false));
            buffer.append(getEndEquation());

            // beta matrix
            buffer.append(getBeginEquation());
            buffer.append(DISPLAY_MATRIX_BETA).append(" = ");
            if (clusterSize > 1) {
                buffer.append(getColumnOfOnesTex(clusterSize, true));
                buffer.append(KRONECKER_PRODUCT);
            }
            buffer.append(realMatrixToTex(null,
                    B.getCombinedMatrix(), false));
            buffer.append(getEndEquation());

            // between participant contrast
            buffer.append(getBeginEquation());
            buffer.append(realMatrixToTex(DISPLAY_MATRIX_BETWEEN_CONTRAST,
                    C.getCombinedMatrix(), false));
            buffer.append(getEndEquation());

            // within participant contrast
            buffer.append(getBeginEquation());
            buffer.append(DISPLAY_MATRIX_WITHIN_CONTRAST).append(" = ");
            if (clusterSize > 1) {
                buffer.append(getColumnOfOnesTex(clusterSize, false));
                buffer.append(KRONECKER_PRODUCT);
            }
            buffer.append(realMatrixToTex(null,
                    U, false));
            buffer.append(getEndEquation());

            // observed theta
            buffer.append(getBeginEquation());
            buffer.append(realMatrixToTex(
                    DISPLAY_MATRIX_THETA_OBSERVED,
                    thetaObserved, false));
            buffer.append(getEndEquation());

            // theta null matrix
            buffer.append(getBeginEquation());
            buffer.append(realMatrixToTex(
                    DISPLAY_MATRIX_THETA_NULL,
                    thetaNull, false));
            buffer.append(getEndEquation());

            // add matrices for either GLMM(F) or GLMM(F,g) designs
            if (studyDesign.isGaussianCovariate()) {
                RealMatrix sigmaY =
                    PowerResourceHelper.sigmaOutcomesMatrixFromStudyDesign(studyDesign);
                RealMatrix sigmaG =
                    PowerResourceHelper.sigmaCovariateMatrixFromStudyDesign(studyDesign);
                RealMatrix sigmaYG =
                        PowerResourceHelper.sigmaOutcomesCovariateMatrixFromStudyDesign(
                                studyDesign, sigmaG, sigmaY);

                // set the sigma error matrix to [sigmaY - sigmaYG * sigmaG-1 * sigmaGY]
                RealMatrix sigmaGY = sigmaYG.transpose();
                RealMatrix sigmaGInverse = new LUDecomposition(sigmaG).getSolver().getInverse();
                RealMatrix sigmaE = sigmaY.subtract(sigmaYG.multiply(sigmaGInverse.multiply(sigmaGY)));
                // TODO: display sigmaE, or quit computing it here!

                // sigma for Gaussian covariate
                buffer.append(getBeginEquation());
                buffer.append(realMatrixToTex(
                        DISPLAY_MATRIX_SIGMA_GAUSSIAN, sigmaG, false));
                buffer.append(getEndEquation());

                // sigma for Gaussian covariate and outcomes
                buffer.append(getBeginEquation());
                buffer.append(DISPLAY_MATRIX_SIGMA_OUTCOME_GAUSSIAN).append(" = ");
                if (clusterSize > 1) {
                    buffer.append(getColumnOfOnesTex(clusterSize, false));
                    buffer.append(KRONECKER_PRODUCT);
                }
                buffer.append(realMatrixToTex(null,
                        sigmaYG, false));
                buffer.append(getEndEquation());

                // sigma outcomes matrix
                studyDesign.setClusteringTree(clusterNodeList);
                buffer.append(getBeginEquation());
                buffer.append(getSigmaOutcomeMatrixTex(studyDesign));
                buffer.append(getEndEquation());
            } else {
                // sigma error
                studyDesign.setClusteringTree(clusterNodeList);
                buffer.append(getBeginEquation());
                buffer.append(getSigmaErrorMatrixTex(studyDesign));
                buffer.append(getEndEquation());
            }

            buffer.append("<p/>(For ease of display, some scaling factors may have been omitted.)");
            buffer.append("<p/>For notation details, please see<p/>");
            buffer.append(createCitations());
            buffer.append(createBrowserNotes());
        } else {
            buffer.append("No study design specified");
        }

        buffer.append("</body></html>");
        return buffer.toString();
    }

    private String getBeginEquation() {
        return "\n<br/>\n\\begin{equation*}\n<br/>\n";
    }

    private String getEndEquation() {
        return "\n<br/>\n\\end{equation*}\n<br/>\n";
    }

    /**
     * Create a matrix representing with a column of 1's using
     * Muller & Stewart 2007 notation.
     *
     * @param size column size
     * @param transpose indicates if the matrix is transposed.
     * @return string representation of matrix
     */
    private String getColumnOfOnesTex(int size, boolean transpose) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\\boldsymbol{1}_{").append(size).append("}");
        if (transpose) {
            buffer.append("'");
        }
        return buffer.toString();
    }

    /**
     * Create MathJax LaTeX for an identity matrix using
     * Muller & Stewart 2007 notation.
     *
     * @param size column size
     * @return string representation of matrix
     */
    private String getIdentityTex(int size) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\\boldsymbol{I}_{").append(size).append("}");
        return buffer.toString();
    }

    /**
     * Create MathJax LaTeX for the sigma error matrix
     * @param studyDesign
     * @return MathJax LaTeX block
     */
    private String getSigmaMatrixTex(String name, StudyDesign studyDesign) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(name).append(" = ");

        // add covariance information for clustering
        boolean first = true;
        List<ClusterNode> clusterNodeList = studyDesign.getClusteringTree();
        if (clusterNodeList != null && clusterNodeList.size() > 0) {
            int clusterSize = 1;
            for(ClusterNode node: clusterNodeList) {
                clusterSize *= node.getGroupSize();
            }
            if (clusterSize > 1) {
                for(ClusterNode clusterNode: clusterNodeList) {
                    if (!first) {
                        buffer.append(KRONECKER_PRODUCT);
                    }
                    int size = clusterNode.getGroupSize();
                    double rho = clusterNode.getIntraClusterCorrelation();
                    buffer.append(getCompoundSymmetricTex(size, rho));
                    if (first) {
                        first = false;
                    }
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
                            buffer.append(KRONECKER_PRODUCT);
                        }
                        buffer.append(realMatrixToTex(null, kroneckerMatrix, false));
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
                buffer.append(KRONECKER_PRODUCT);
            }
            buffer.append(realMatrixToTex(null, kroneckerMatrix, false));
        }

        return buffer.toString();
    }

    /**
     * Get LaTeX for a compound symmetric correlation matrix
     * @param size size of the matrix
     * @param rho correlation parameter
     * @return MathJax LaTeX for matrix
     */
    private String getCompoundSymmetricTex(int size, double rho) {

        // append a compound symmetric correlation matrix
        StringBuilder buffer = new StringBuilder();
        buffer.append("\\left[");
        buffer.append(getColumnOfOnesTex(size,false));
        buffer.append(getColumnOfOnesTex(size,true));
        buffer.append("\\left(");
        buffer.append(format(rho));
        buffer.append("\\right)");
        buffer.append(" + ");
        buffer.append(getIdentityTex(size));
        buffer.append("\\left(1 - ");
        buffer.append(format(rho));
        buffer.append("\\right)");
        buffer.append("\\right]");

        return buffer.toString();
    }

    /**
     * Create MathJax LaTeX for the sigma error matrix
     * @param studyDesign
     * @return MathJax LaTeX block
     */
    private String getSigmaErrorMatrixTex(StudyDesign studyDesign) {
        if (studyDesign.getViewTypeEnum() == StudyDesignViewTypeEnum.MATRIX_MODE) {
            return realMatrixToTex(
                    DISPLAY_MATRIX_SIGMA_ERROR,
                    PowerResourceHelper.toRealMatrix(
                            studyDesign.getNamedMatrix(
                                    PowerConstants.MATRIX_SIGMA_ERROR)),
                            false);
        } else {
            return getSigmaMatrixTex(
                    DISPLAY_MATRIX_SIGMA_ERROR,
                    studyDesign);
        }
    }

    /**
     * Create MathJax LaTeX for the sigma error matrix
     * @param studyDesign
     * @return MathJax LaTeX block
     */
    private String getSigmaOutcomeMatrixTex(StudyDesign studyDesign) {
        if (studyDesign.getViewTypeEnum() == StudyDesignViewTypeEnum.MATRIX_MODE) {
            return realMatrixToTex(
                    DISPLAY_MATRIX_SIGMA_OUTCOME,
                    PowerResourceHelper.toRealMatrix(
                            studyDesign.getNamedMatrix(
                                    PowerConstants.MATRIX_SIGMA_OUTCOME)),
                            false);
        } else {
            return getSigmaMatrixTex(
                    DISPLAY_MATRIX_SIGMA_OUTCOME,
                    studyDesign);
        }
    }

    /**
     * Create a LaTeX representation of the matrix
     * @param name
     * @param baseMatrix
     * @param transpose
     * @return LaTeX block
     */
    private String realMatrixToTex(String name, RealMatrix baseMatrix, boolean transpose) {
        StringBuilder buffer = new StringBuilder();
        if (baseMatrix != null) {
            RealMatrix matrix = baseMatrix;
            if (transpose) {
                matrix = baseMatrix.transpose();
            }

            int rows = matrix.getRowDimension();
            int columns = matrix.getColumnDimension();

            if (name != null) {
                buffer.append(name).append(" = ");
            }

            buffer.append(LATEX_MATRIX_BEGIN);
            for(int row = 0; row < rows; row++) {
                if (row > 0) {
                    buffer.append(" \\\\ ");
                }
                for(int col = 0; col < columns; col++) {
                    if (col > 0) {
                        buffer.append(" & ");
                    }
                    buffer.append(format(matrix.getEntry(row, col)));
                }

            }
            buffer.append(LATEX_MATRIX_END);

            if (transpose) {
                buffer.append("'");
            }
        }

        return buffer.toString();
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
        return "<p/><div ng-show=\"!isMobile\">" +
                "This feature requires browser support of MathJax.  Please see the " +
                "<a target=\"_blank\" href=\"http://www.mathjax.org/\">MathJax Homepage</a> " +
                "for information regarding supported browsers.</div>";
    }

    private static String format(double d) {
        String s = formatter.format(d);
        return s.equals("-0.0000") ? "0.0000" : s;
    }

    private static ResourceException badRequestException(String message) {
        return new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
    }
}
