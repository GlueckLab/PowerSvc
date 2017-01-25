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

import org.restlet.resource.Post;

import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

/**
 * Main interface for calculating an HTML/MathJax representation of the matrices
 * used in a power calculation.
 *
 * @author Sarah Kreidler
 */
public interface PowerMatrixHTMLResource {
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
    String getMatricesAsHTML(String jsonStudyDesign);

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
    String getMatricesAsHTML(StudyDesign studyDesign);
}
