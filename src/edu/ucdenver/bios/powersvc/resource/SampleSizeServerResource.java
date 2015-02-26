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

import java.util.List;
import java.util.concurrent.*;

import edu.ucdenver.bios.powersvc.application.JsonLogger;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
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
 * Implementation of the SampleSizeResource interface for calculating
 * sample size.
 *
 * @author Sarah Kreidler
 */
public class SampleSizeServerResource extends ServerResource
implements SampleSizeResource {

    private Logger logger = Logger.getLogger(getClass());

    private static final ExecutorService THREADS = Executors.newCachedThreadPool();

    private static final int BYTES_PER_MEG = 1024 * 1024;

    /**
	 * Calculate the total sample size for the specified study design.
	 * 
	 * @param studyDesign study design object
	 * @return List of power objects for the study design.  These will contain the total sample size
	 */
	public PowerResultList getSampleSize(StudyDesign studyDesign)
	{
        JsonLogger.logObject("SampleSizeServerResource.getSampleSize(): " + getRequest().getRootRef().toString() +
                getRequest().getRootRef().toString() + ": studyDesign = ", studyDesign);
        logger.info("Memory stats: free: " + Runtime.getRuntime().freeMemory() / BYTES_PER_MEG +
                "M, total: " + Runtime.getRuntime().totalMemory() / BYTES_PER_MEG +
                "M, max: " + Runtime.getRuntime().maxMemory() / BYTES_PER_MEG + "M");
        long start = System.currentTimeMillis();

        // Execute the calculation in asynchronously and time out after 300 seconds.  User
        // gets an error
        SampleSizeCallable callable = new SampleSizeCallable(studyDesign);
        Future<PowerResultList> future = THREADS.submit(callable);
        try {
            PowerResultList results = future.get(300, TimeUnit.SECONDS);
            logger.info("getSampleSize(): executed in " + Long.toString(System.currentTimeMillis() - start) + " milliseconds");
            return results;
        } catch (InterruptedException e) {
            logger.warn(getClass().getSimpleName() + ": InterruptedException(): " + getRequest().getRootRef().toString(), e);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Computation interrupted");
        } catch (ExecutionException e) {
            logger.warn(getClass().getSimpleName() + ": ExecutionException(): " + getRequest().getRootRef().toString(), e);
            if (e.getCause() instanceof PowerException) {
                PowerException pe = (PowerException) e.getCause();
                PowerLogger.getInstance().error("[" + pe.getErrorCode() + "]:" + pe.getMessage());
            }
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Exception during computation");
        } catch (TimeoutException e) {
            logger.warn(getClass().getSimpleName() + ": TimeoutException(): " + getRequest().getRootRef().toString());
            logger.warn(getClass().getSimpleName() + ": TimeoutException(): " + JsonLogger.toJson(studyDesign));
            boolean canceled = future.cancel(true);
            logger.info(getClass().getSimpleName() + ": canceled: " + canceled);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "Request timed out during computation");
        }
    }

    public static class SampleSizeCallable implements Callable<PowerResultList> {

        private StudyDesign studyDesign;

        private SampleSizeCallable(StudyDesign studyDesign) {
            if (studyDesign == null)
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                        "Invalid study design");
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
                List<Power> calcResults = calculator.getSampleSize(params);
                // convert to concrete classes
                return PowerResourceHelper.toPowerResultList(calcResults);
            } catch (IllegalArgumentException iae) {
                PowerLogger.getInstance().error(iae.getMessage());
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, iae.getMessage());
            } catch (PowerException pe) {
                PowerLogger.getInstance().error("[" + pe.getErrorCode() + "]:" + pe.getMessage());
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                        pe.getMessage());
            }

        }
    }
}
