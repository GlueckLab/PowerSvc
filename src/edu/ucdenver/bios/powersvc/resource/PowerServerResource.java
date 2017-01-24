/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 *
 * Copyright (C) 2016 Regents of the University of Colorado.
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
import java.util.List;
import java.util.concurrent.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.cudenver.bios.power.GLMMPowerCalculator;
import edu.cudenver.bios.power.Power;
import edu.cudenver.bios.power.PowerException;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.ucdenver.bios.powersvc.application.PowerLogger;
import edu.ucdenver.bios.webservice.common.domain.PowerResultList;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

/**
 * Implementation of the PowerResource interface
 * for calculating power.
 *
 * @author Sarah Kreidler
 */
public class PowerServerResource extends ServerResource
        implements PowerResource {
    private Logger logger = Logger.getLogger(getClass());

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int BYTES_PER_MEG = 1024 * 1024;

    private static final ExecutorService THREADS = Executors.newCachedThreadPool();

    /**
     * Calculate power for the specified study design JSON.
     *
     * @param jsonStudyDesign study design JSON
     *
     * @return JSON representation of the list of power objects
     *         for the study design
     */
    @Post
    public final String getPower(final String jsonStudyDesign) {
        if (jsonStudyDesign == null) {
            throw badRequestException("Invalid study design.");
        }

        logger.info("PowerServerResource.getPower(): " + getRequest().getRootRef() + ": "
                        + "jsonStudyDesign = '" + jsonStudyDesign + "'");

        StudyDesign studyDesign;

        try {
            studyDesign = MAPPER.readValue(jsonStudyDesign, StudyDesign.class);
        } catch (IOException ioe) {
            PowerLogger.getInstance().error(ioe.getMessage(), ioe);
            throw badRequestException(ioe.getMessage());
        }

        PowerResultList powerResultList = getPower(studyDesign, jsonStudyDesign);

        String result;

        try {
            result = MAPPER.writeValueAsString(powerResultList);
        } catch (IOException ioe) {
            PowerLogger.getInstance().error(ioe.getMessage(), ioe);
            throw badRequestException(ioe.getMessage());
        }

        logger.info("INPUT = '" + jsonStudyDesign + "'");
        logger.info("OUTPUT = '" + result + "'");


        return result;
    }

    /**
     * Calculate power for the specified study design object.
     * This is only called by test code.
     *
     * @param studyDesign study design object
     *
     * @return List of power objects for the study design
     */
    public final PowerResultList getPower(final StudyDesign studyDesign) {
        return getPower(studyDesign, "NOT SUPPLIED");
    }

    /**
     * Calculate power for the specified study design object, and, possibly,
     * the study design JSON from whence it came.
     *
     * @param studyDesign study design object
     * @param jsonStudyDesign study design JSON, if available
     *
     * @return List of power objects for the study design
     */
    private final PowerResultList getPower(final StudyDesign studyDesign, final String jsonStudyDesign) {
        if (studyDesign == null) {
            throw badRequestException("Invalid study design.");
        }

        logger.info("Memory stats: free: " + Runtime.getRuntime().freeMemory() / BYTES_PER_MEG +
                "M, total: " + Runtime.getRuntime().totalMemory() / BYTES_PER_MEG +
                "M, max: " + Runtime.getRuntime().maxMemory() / BYTES_PER_MEG + "M");
        long start = System.currentTimeMillis();

        // Execute the calculation asynchronously and time out after a certain period of time.
        PowerCallable callable = new PowerCallable(studyDesign);
        Future<PowerResultList> future = THREADS.submit(callable);
        try {
            // TODO: make the timeout configurable
            PowerResultList results = future.get(300, TimeUnit.SECONDS);
            logger.info("getPower(): " + "executed in " + (System.currentTimeMillis() - start) + " milliseconds");
            return results;
        } catch (InterruptedException e) {
            logger.warn(getClass().getSimpleName() + ": InterruptedException(): " + getRequest().getRootRef(), e);
            throw badRequestException("Computation interrupted.");
        } catch (ExecutionException e) {
            logger.warn(getClass().getSimpleName() + ": ExecutionException(): " + getRequest().getRootRef(), e);
            Throwable cause = e.getCause();
            if (cause instanceof PowerException) {
                PowerException pe = (PowerException) cause;
                PowerLogger.getInstance().error("[" + pe.getErrorCode() + "]:" + pe.getMessage());
            }
            if (cause instanceof ResourceException) {
                ResourceException re = (ResourceException) cause;
                Status status = re.getStatus();
                if (Status.CLIENT_ERROR_BAD_REQUEST.equals(status)) {
                    throw re;
                }
            }
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Exception during computation");
        } catch (TimeoutException e) {
            logger.warn(getClass().getSimpleName() + ": TimeoutException(): " + getRequest().getRootRef());
            logger.warn(getClass().getSimpleName() + ": TimeoutException(): " + jsonStudyDesign);
            boolean canceled = future.cancel(true);
            logger.info(getClass().getSimpleName() + ": canceled: " + canceled);
            throw badRequestException("Request timed out during computation.");
        }
    }

    public static class PowerCallable implements Callable<PowerResultList> {
        private StudyDesign studyDesign;

        private PowerCallable(StudyDesign studyDesign) {
            this.studyDesign = studyDesign;
        }

        @Override
        public PowerResultList call() throws Exception {
            try {
                GLMMPowerParameters params =
                        PowerResourceHelper.studyDesignToPowerParameters(studyDesign);
                // create the appropriate power calculator for this model
                GLMMPowerCalculator calculator = new GLMMPowerCalculator();
                // calculate the power results
                List<Power> calcResults = calculator.getPower(params);
                // convert to concrete classes
                return PowerResourceHelper.toPowerResultList(calcResults);
            } catch (IllegalArgumentException iae) {
                PowerLogger.getInstance().error(iae.getMessage(), iae);
                throw badRequestException(iae.getMessage());
            } catch (PowerException pe) {
                PowerLogger.getInstance().error("[" + pe.getErrorCode() + "]:" + pe.getMessage(), pe);
                throw badRequestException(pe.getMessage());
            } catch (OutOfMemoryError oome) {
                PowerLogger.getInstance().error(oome.getMessage(), oome);
                throw badRequestException("Insufficient memory to process this study design.");
            }
        }
    }

    private static ResourceException badRequestException(String message) {
        return new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
    }
}
