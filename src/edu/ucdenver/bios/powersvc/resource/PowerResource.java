/*
 * Power Service for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 *
 * Copyright (C) 2015 Regents of the University of Colorado.
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

import org.restlet.resource.Post;

import edu.ucdenver.bios.webservice.common.domain.PowerResultList;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

/**
 * Main interface for calculating power.
 *
 * @author Sarah Kreidler
 */
public interface PowerResource {
    /**
     * Calculate power for the specified study design JSON.
     *
     * @param jsonStudyDesign study design JSON
     *
     * @return JSON representation of the list of power objects
     *         for the study design
     */
    @Post
    String getPower(String jsonStudyDesign);

    /**
     * Calculate power for the specified study design object.
     * This is only called by test code.
     *
     * @param studyDesign study design object
     *
     * @return List of power objects for the study design
     */
    PowerResultList getPower(StudyDesign studyDesign);
}
