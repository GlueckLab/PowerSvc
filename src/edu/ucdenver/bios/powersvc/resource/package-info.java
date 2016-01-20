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

/**
* Restlet resources which process incoming requests based on the incoming
* URI structure.  The URI mappings are defined in the edu.cudenver.bios.powersvc.application.PowerApplication
* class.
*
* <h2>Package Specification</h2>
*
* Dependencies:
* <ul>
* <li>JRE 1.6.0 or higher</li>
* <li>Restlet 2.0.10</li>
* <li>Log4J 1.2.15</li>
* <li>JUnit 4.7</li>
* <li>Apache Commons Math 2.1 or higher</li>
* <li><a href="http://www.jsc.nildram.co.uk/">JSC Statistics Package</a></li>
* </ul>
*
* <h2>Related Documentation</h2>
*
* This package is part of the Power web service component for the Glimmpse software system, please see
* the following for more information:
* <ul>
*   <li><a href="http://www.glimmpse.com/">http://www.glimmpse.com/</a>
* </ul>
 */
package edu.ucdenver.bios.powersvc.resource;



