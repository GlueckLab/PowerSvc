package edu.cudenver.bios.powersvc.application;

public class PowerConstants
{
    // maximum allowed simulation size
    public static final int MAX_SIMULATION_SIZE = 100000;
    public static final int DEFAULT_SIMULATION_SIZE = 10000;
    
    // valid model names
    public static final String TEST_ONE_SAMPLE_STUDENT_T = "onesamplestudentt";
    public static final String TEST_GLMM = "glmm";
    
    // URI and query parameters
    public static final String REQUEST_ITERATIONS = "iterations";
    
    // xml tag names
    public static final String TAG_CURVE = "curve";
    public static final String TAG_CURVE_IMG = "curveImg";
    public static final String TAG_ERROR = "error";
    public static final String TAG_POWER = "power";
    public static final String TAG_SAMPLESIZE = "sampleSize";
    public static final String TAG_PARAMS = "params";
    public static final String TAG_ESSENCE_MATRIX = "essenceMatrix";
    public static final String TAG_MATRIX = "matrix";
    public static final String TAG_ROW = "r";
    public static final String TAG_COLUMN = "c";
    public static final String TAG_ROW_META_DATA = "rowMetaData";
    public static final String TAG_COLUMN_META_DATA = "columnMetaData";
    public static final String ATTR_MODEL = "modelName";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_MU0 = "mu0";
    public static final String ATTR_MUA = "muA";
    public static final String ATTR_SIGMA_ERROR = "sigmaError";
    public static final String ATTR_ALPHA = "alpha";
    public static final String ATTR_SAMPLESIZE = "sampleSize";
    public static final String ATTR_CURVE_WIDTH = "width";
    public static final String ATTR_CURVE_HEIGHT = "height";
    public static final String ATTR_CURVE_TITLE = "title";
    public static final String ATTR_CURVE_XLABEL = "xlabel";
    public static final String ATTR_CURVE_YLABEL = "ylabel";
    public static final String ATTR_CALCULATED = "calculated";
    public static final String ATTR_SIMULATED = "simulated";
    public static final String ATTR_SIMULATION_SIZE = "simulationSize";
    public static final String ATTR_ONE_TAILED = "oneTailed";
    public static final String ATTR_POWER = "power";
    public static final String ATTR_REPETITIONS = "reps";
    public static final String ATTR_RATIO = "ratio";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_MEAN = "mean";
    public static final String ATTR_VARIANCE = "variance";
    public static final String ATTR_ROWS = "rows";
    public static final String ATTR_COLUMNS = "columns";
    public static final String ATTR_STATISTIC = "statistic";
    public static final String ATTR_POWER_METHOD = "powerMethod";
    public static final String ATTR_QUANTILE = "quantile";
    public static final String ATTR_RANDOM_SEED = "seed";
    public static final String ATTR_UNIREP_CORRECT = "unirepCorrection";
    public static final String ATTR_UNIREP_CDF = "unirepCdf";
    public static final String ATTR_MOMENT_METHOD = "momentMethod";
    
    // statistic name constants
    public static final String STATISTIC_HOTELLING_LAWLEY_TRACE = "hlt";
    public static final String STATISTIC_WILKS_LAMBDA = "wl";
    public static final String STATISTIC_PILLAU_BARTLETT_TRACE = "pbt";
    public static final String STATISTIC_UNIREP = "unirep";
    
    // types of power approximations
    public static final String POWER_METHOD_CONDITIONAL = "conditional";
    public static final String POWER_METHOD_UNCONDITIONAL = "unconditional";
    public static final String POWER_METHOD_QUANTILE = "quantile";
    
    // univariate approach to repeated measures correction options
    public static final String UNIREP_CORRECT_GEISSER_GREENHOUSE = "gg";
    public static final String UNIREP_CORRECT_HUYNH_FELDT = "hf";
    public static final String UNIREP_CORRECT_BOX = "box";
    public static final String UNIREP_CORRECT_NONE = "none";
    
    // univariate approach to repeated measures  cdf methods
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
