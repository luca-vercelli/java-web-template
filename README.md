# java-web-template
Template for real-world Java Web project.

Goal of this project: set up a new real-world Jave Web project in minutes, without waste of time.
Moreover we want to use "best practice"'s wherever possible.

## DISCLAIMER
Modern applications should be plain HTML, with data loaded asincronously via Ajax. RESt, OData, JSON technologies should be used.
So we consider Struts, Spring, JSTL frameworks obsolete (tell me if and why not).

For development, a good webserver and a good database should not require any configuration. Embedded technologies can be used. We have chosen embedded GlassFish and embedded Derby.

We like EE technologies such as JTA, EJB, Injection, JAX-RS, so we cannot support Tomcat and Jetty. You can consider using TomEE. JetSet is not ready jet. 

Tools:
* eclipse
* java 8
* maven 3
* Embedded Glassfish as WAS
* Embedded Derby as database, configured as JTA datasource
* REST: JAX-RS
* MVC: jstl (needed?)
* persistence: JPA + hibernate 5 + annotations
* transactions: JTA + EJB
* connection pool: commons DBCP? C3PO?
* logging: commons logging + log4j
* Gentelella 1.4 frontend (jquery 3 + bootstrap 3)
* testing: junit 4
* javax.emails
* JAAS ?

Features:
* JSP templating (and not struts tiles plugin)
* CRUD (via RESTful services)
* login page
* accounts management
* domain login
* authorizations
* all features as /modules/ so that they can be freely used or not

Modules and interface should be independent: standalone Swing, or AngularJS frontends should work as well.

