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
package edu.ucdenver.bios.powersvc.application;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonLogger {
    private static Logger logger = Logger.getLogger(JsonLogger.class);

    private static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Convert the input object to a JSON string and return it.  If the
     * input object cannot be converted then its toString() method is
     * called.  No exceptions are thrown.
     *
     * @param obj object to convert to JSON
     * @return the JSON representation of the input object
     */
    public static String toJson(Object obj) {
        String json;
        if (obj == null) {
            json = "null";
        } else {
            try {
                json = MAPPER.writeValueAsString(obj);
            } catch (IOException e) {
                json = obj.toString();
                logger.warn("Unable to map object to string: " + json, e);
            }
        }
        return json;
    }
}
