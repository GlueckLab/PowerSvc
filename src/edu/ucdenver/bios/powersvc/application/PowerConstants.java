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
package edu.ucdenver.bios.powersvc.application;

/**
 * Convenience class for power service constants.
 *
 * @author Sarah Kreidler
 */
public final class PowerConstants {

    /** Private constructor ensures this class cannot be instantiated. */
    private PowerConstants() { }
    /** Current version. */
    public static final String VERSION = "2.0.0beta";

    /** Maximum allowed simulation size. */
    public static final int MAX_SIMULATION_SIZE = 100000;
    /** Default simulation size. */
    public static final int DEFAULT_SIMULATION_SIZE = 10000;
    
    // type name constants
    public static final String MATRIX_BETA = "beta";
    public static final String MATRIX_BETA_RANDOM = "betaRandom";
    public static final String MATRIX_DESIGN = "design";
    public static final String MATRIX_THETA_NULL = "thetaNull";
    public static final String MATRIX_WITHIN_CONTRAST = "withinSubjectContrast";
    public static final String MATRIX_BETWEEN_CONTRAST = "betweenSubjectContrast";
    public static final String MATRIX_BETWEEN_CONTRAST_RANDOM = "betweenSubjectContrastRandom";
    public static final String MATRIX_SIGMA_ERROR = "sigmaError";
    public static final String MATRIX_SIGMA_GAUSSIAN = "sigmaGaussianRandom";
    public static final String MATRIX_SIGMA_OUTCOME = "sigmaOutcome";
    public static final String MATRIX_SIGMA_OUTCOME_GAUSSIAN = "sigmaOutcomeGaussianRandom";
    public static final String RESPONSES_COVARIANCE_LABEL = "__RESPONSE_COVARIANCE__";
    
    // display names for matrices (used in MatrixHTML resource)
    public static final String BOLD_OPEN = "<mstyle mathvariant=\"bold\" mathsize=\"normal\">";
    public static final String BOLD_CLOSE = "</mstyle>";
    public static final String DISPLAY_MATRIX_BETA = BOLD_OPEN + "<mi>B</mi>" + BOLD_CLOSE;
    public static final String DISPLAY_MATRIX_DESIGN = 
            "<mrow><mtext>Es</mtext><mo>(</mo>"+ BOLD_OPEN + "<mi>X</mi>" + BOLD_CLOSE + "<mo>)</mo></mrow>";
    public static final String DISPLAY_MATRIX_THETA_NULL = "<msub>"+ BOLD_OPEN + 
            "<mi>&#x0398;</mi>" + BOLD_CLOSE + "<mn>0</mn></msub>";
    public static final String DISPLAY_MATRIX_WITHIN_CONTRAST = BOLD_OPEN + "<mi>U</mi>" + BOLD_CLOSE;
    public static final String DISPLAY_MATRIX_BETWEEN_CONTRAST = BOLD_OPEN + "<mi>C</mi>" + BOLD_CLOSE;
    public static final String DISPLAY_MATRIX_SIGMA_ERROR = "<msub>" + BOLD_OPEN +
            "<mi>&#x03A3;</mi>"+ BOLD_CLOSE + "<mi>E</mi></msub>";
    public static final String DISPLAY_MATRIX_SIGMA_GAUSSIAN = "<msub>" + BOLD_OPEN +
            "<mi>&#x03A3;</mi>"+ BOLD_CLOSE + "<mi>g</mi></msub>";
    public static final String DISPLAY_MATRIX_SIGMA_OUTCOME = "<msub>" + BOLD_OPEN +
            "<mi>&#x03A3;</mi>"+ BOLD_CLOSE + "<mi>Y</mi></msub>";
    public static final String DISPLAY_MATRIX_SIGMA_OUTCOME_GAUSSIAN = "<msub>" + BOLD_OPEN +
            "<mi>&#x03A3;</mi>"+ BOLD_CLOSE + "<mi>Yg</mi></msub>";
}
