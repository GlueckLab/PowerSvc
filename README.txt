 Power Service for the GLIMMPSE Software System.  Processes
 incoming HTTP requests for power, sample size, and detectable
 difference
  
 Copyright (C) 2010 Regents of the University of Colorado.  
 
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 
------------------------------
1. INTRODUCTION
------------------------------

This web service process power/sample size/detectable difference requests from
the Glimmpse user interface.  It is a component of the Glimmpse software system 
(http://www.glimmpse.com/)

The power calculations are based on the publication:

Glueck DH and Muller KE.  Adjusting power for a baseline covariate in linear models.
Statist. Med. 2003; 22:2535–2551.

------------------------------
2.  LATEST VERSION
------------------------------

Version 1.0.0

------------------------------
3.  DOCUMENTATION
------------------------------

Documentation is available from the project web site:

http://www.glimmpse.com/

The REST interface documentation is included with the source distribution in:

${POWER_SERVICE_HOME}/docs/api.html

------------------------------
4. DEPENDENCIES
------------------------------

This web service has been tested in Apache Tomcat 6.x

==Third-party dependencies==

Java Runtime Environment 1.6.0 or higher
Apache Commons Math 2.1 or higher
JSC Statistics Package (http://www.jsc.nildram.co.uk/)
Restlet 1.1.6 (org.restlet.jar, com.noelios.restlet.jar, com.noelios.restlet.ext.servlet_2.5.jar)
JUnit 4.7
Log4j 1.2.15
Apache Ant 1.8.1

------------------------------
5.  SUPPORT
------------------------------

This web service is provided without warranty.

For questions regarding this web service, please email sarah.kreidler@ucdenver.edu

------------------------------
6.  ANT BUILD SCRIPT
------------------------------

The main build.xml script is located in the ${POWER_SERVICE_HOME}/build
directory.  To compile the library, cd to ${POWER_SERVICE_HOME}/build
and type

cd to ${POWER_SERVICE_HOME}/build
ant

The resulting war file is called

${POWER_SERVICE_HOME}/tmpbin/power.war

------------------------------
7. CONTRIBUTORS / ACKNOWLEDGEMENTS
------------------------------

This library was created by Dr. Sarah Kreidler and Dr. Deb Glueck
at the University of Colorado Denver.

Special thanks to the following individuals were instrumental in completion of this project:
Dr. Keith Muller
Dr. Anis Karimpour-Fard
Dr. Jackie Johnson

