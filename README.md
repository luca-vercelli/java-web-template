# java-web-template
Template for real-world Java Web project

Tools:
* eclipse
* java 8
* maven 3
* struts 2 (maybe + annotations?)
* JPA + hibernate 5 + JTA + annotations + commons DBCP
* commons logging + log4j
* junit 4
* SQLite as database (just for test)
* Apache Tomcat v8
* datasource? (webserver-dependent)
* emails? (webserver-dependent, not supported by Tomcat)

Features:
* JSP templating
* CRUD (voia RESTful services)
* login page
* accounts management
* domain login
* authorizations
* all features as /modules/ so that they can be freely used or not

This template is intended for small/medium intranet web applications. No EJB's here.

Modules and interface should be independent: standalone Swing, or plain-HTML+AngularJS frontends should work as well.