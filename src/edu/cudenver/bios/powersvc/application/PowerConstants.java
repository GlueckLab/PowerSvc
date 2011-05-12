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
package edu.cudenver.bios.powersvc.application;

/**
 * Convenience class for power service constants
 * 
 * @author Sarah Kreidler
 */
public class PowerConstants
{
	// current version
	public static final String VERSION = "1.0.0";
	
    // maximum allowed simulation size
    public static final int MAX_SIMULATION_SIZE = 100000;
    public static final int DEFAULT_SIMULATION_SIZE = 10000;
    
    // URI and query parameters
    public static final String REQUEST_ITERATIONS = "iterations";
    
    // XML tag names
    public static final String TAG_ERROR = "error";
    public static final String TAG_GLMM_POWER = "glmmPower";
    public static final String TAG_GLMM_POWER_PARAMETERS = "glmmPowerParameters";
    public static final String TAG_FIXED_RANDOM_MATRIX = "fixedRandomMatrix";
    public static final String TAG_MATRIX = "matrix";
    public static final String TAG_ROW = "r";
    public static final String TAG_COLUMN = "c";
    public static final String TAG_TEST_LIST = "testList";
    public static final String TAG_ALPHA_LIST = "alphaList";
    public static final String TAG_POWER_LIST = "powerList";
    public static final String TAG_SAMPLE_SIZE_LIST = "sampleSizeList";
    public static final String TAG_BETA_SCALE_LIST = "betaScaleList";
    public static final String TAG_SIGMA_SCALE_LIST = "sigmaScaleList";
    public static final String TAG_POWER_METHOD_LIST = "powerMethodList";
    public static final String TAG_QUANTILE_LIST = "quantileList";
    public static final String TAG_CONFIDENCE_INTERVAL = "confidenceInterval";
    
    // XML attribute names
    public static final String ATTR_COUNT = "count";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_POWER = "power";
    public static final String ATTR_CI_ALPHA_LOWER = "alphaLower";
    public static final String ATTR_CI_ALPHA_UPPER = "alphaUpper";
    public static final String ATTR_CI_LOWER = "ciLower";
    public static final String ATTR_CI_UPPER = "ciUpper";
    public static final String ATTR_CI_ESTIMATES_SAMPLE_SIZE = "estimatesSampleSize";
    public static final String ATTR_CI_ESTIMATES_RANK = "estimatesRank";
    public static final String ATTR_RATIO = "ratio";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_MEAN = "mean";
    public static final String ATTR_VARIANCE = "variance";
    public static final String ATTR_ROWS = "rows";
    public static final String ATTR_COLUMNS = "columns";
    public static final String ATTR_RANDOM_SEED = "seed";
    public static final String ATTR_UNIREP_CDF = "unirepCdf";
    public static final String ATTR_MOMENT_METHOD = "momentMethod";
    public static final String ATTR_NOMINAL_POWER = "nominalPower";
    public static final String ATTR_ACTUAL_POWER = "actualPower";
    public static final String ATTR_POWER_METHOD = "powerMethod";
    public static final String ATTR_QUANTILE = "quantile";
    public static final String ATTR_BETA_SCALE = "betaScale";
    public static final String ATTR_SIGMA_SCALE = "sigmaScale";
    public static final String ATTR_ALPHA = "alpha";
    public static final String ATTR_SAMPLE_SIZE = "sampleSize";
    public static final String ATTR_TEST = "test";
    public static final String ATTR_FIXED = "fixed";
    public static final String ATTR_RANDOM = "random";
    public static final String ATTR_COMBINE_HORIZONTAL = "combineHorizontal";

    // statistical test name constants
    public static final String TEST_HOTELLING_LAWLEY_TRACE = "hlt";
    public static final String TEST_PILLAI_BARTLETT_TRACE = "pbt";
    public static final String TEST_WILKS_LAMBDA = "wl";
    public static final String TEST_UNIREP = "unirep";
    public static final String TEST_UNIREP_BOX = "unirepBox";
    public static final String TEST_UNIREP_GG = "unirepGG";
    public static final String TEST_UNIREP_HF = "unirepHF";
    
    // types of confidence intervals
    // known beta coefficients, sigma estimated
    public static final String CONFIDENCE_INTERVAL_BETA_KNOWN_EST_SIGMA = "sigma";
    // both beta and sigma are estimated
    public static final String CONFIDENCE_INTERVAL_EST_BETA_SIGMA = "betaSigma";
    
    // types of power approximations
    public static final String POWER_METHOD_CONDITIONAL = "conditional";
    public static final String POWER_METHOD_UNCONDITIONAL = "unconditional";
    public static final String POWER_METHOD_QUANTILE = "quantile";
    
    // univariate approach to repeated measures cdf methods
    public static final String UNIREP_CDF_MULLER_BARTON_APPROX = "mba";
    public static final String UNIREP_CDF_MULLER_EDWARDS_TAYLOR_APPROX = "meta";
    public static final String UNIREP_CDF_MULLER_EDWARDS_TAYLOR_EXACT = "mete";
    public static final String UNIREP_CDF_MULLER_EDWARDS_TAYLOR_EXACT_APPROX = "metea";
    
    // moment approximation method
    public static final String MOMENT_METHOD_PILLAI_ONE_MOMENT = "pillai1";
    public static final String MOMENT_METHOD_PILLAI_ONE_MOMENT_OMEGA_MULT = "pillai1mult";
    public static final String MOMENT_METHOD_MCKEON_TWO_MOMENT = "mckeon2";
    public static final String MOMENT_METHOD_MCKEON_TWO_MOMENT_OMEGA_MULT = "mckeon2mult";
    public static final String MOMENT_METHOD_MULLER_TWO_MOMENT = "muller2";
    public static final String MOMENT_METHOD_MULLER_TWO_MOMENT_OMEGA_MULT = "muller2mult";
    public static final String MOMENT_METHOD_RAO_TWO_MOMENT = "rao2";
    public static final String MOMENT_METHOD_RAO_TWO_MOMENT_OMEGA_MULT = "rao2mult";
    
    // predictor type constants
    public static final String COLUMN_TYPE_FIXED = "fixed";
    public static final String COLUMN_TYPE_RANDOM = "random";
    
    // type name constants
    public static final String MATRIX_TYPE_BETA = "beta";
    public static final String MATRIX_TYPE_DESIGN = "design";
    public static final String MATRIX_TYPE_THETA = "theta";
    public static final String MATRIX_TYPE_WITHIN_CONTRAST = "withinSubjectContrast";
    public static final String MATRIX_TYPE_BETWEEN_CONTRAST = "betweenSubjectContrast";
    public static final String MATRIX_TYPE_SIGMA_ERROR = "sigmaError";
    public static final String MATRIX_TYPE_SIGMA_GAUSSIAN = "sigmaGaussianRandom";
    public static final String MATRIX_TYPE_SIGMA_OUTCOME = "sigmaOutcome";
    public static final String MATRIX_TYPE_SIGMA_OUTCOME_GAUSSIAN = "sigmaOutcomeGaussianRandom";
    
}
