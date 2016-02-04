 Power Web Service for the GLIMMPSE Software System.  Processes
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

Th Power web service calculates power, sample size, or detectable difference for
the Glimmpse Web user interface.  It is a component of the Glimmpse software system
(http://glimmpse.samplesizeshop.com/)

The power calculations are based on the work of Professor Keith E. Muller
and colleagues.  A full list of related publications are available at:

http://samplesizeshop.com/education/related-publications/

------------------------------
2.  LATEST VERSION
------------------------------

Version 2.0.0

------------------------------
3.  DOCUMENTATION
------------------------------

Documentation is available from the project web site:

http://samplesizeshop.com/documentation/glimmpse/

------------------------------
4. DEPENDENCIES
------------------------------

This web service has been tested in Apache Tomcat 6.x and 7.x

==Third-party dependencies==

Java Runtime Environment 1.6.0 or higher
Apache Commons Math 2.1 or higher
JSC Statistics Package (http://www.jsc.nildram.co.uk/)
Restlet 2.0.x
JUnit 4.7
Log4j 1.2.15
Apache Ant 1.8.1

------------------------------
5.  SUPPORT
------------------------------

The Power web service is provided without warranty.

For questions regarding this web service, please email sarah.kreidler@ucdenver.edu

------------------------------
6.  ANT BUILD SCRIPT
------------------------------

The main build.xml script is located in the ${POWER_SERVICE_HOME}/build
directory.  To compile the application, change to the ${POWER_SERVICE_HOME}/build
directory and type

ant

The resulting war file is called

${POWER_SERVICE_HOME}/build/artifacts/power.war

The build script assumes that the a directory called thirdparty is
installed at the same directory level as ${POWER_SERVICE_HOME}.
A thirdparty distribution in the appropriate format is available from

http://samplesizeshop.com/software-downloads/glimmpse-software-downloads/

------------------------------
7. CONTRIBUTORS / ACKNOWLEDGEMENTS
------------------------------

The Power web service was created by Dr. Sarah Kreidler and Dr. Deb Glueck
at the University of Colorado Denver, Department of Biostatistics and Informatics.

Special thanks to the following individuals were instrumental in completion of this project:
Professor Keith E. Muller
Dr. Anis Karimpour-Fard
Dr. Jackie Johnson

