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
}
