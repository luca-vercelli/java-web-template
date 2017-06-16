# java-web-template
Template for real-world Java Web project.

Goal of this project: set up a new real-world Jave Web project in minutes, without waste of time.
Moreover we want to use "best practice"'s wherever possible.

## DISCLAIMER
Modern applications should be plain HTML, with data loaded asincronously via Ajax. RESt, OData, JSON technologies should be used.
So we consider Struts, Spring, JSF frameworks obsolete (tell me if and why not).
JSP/JSTL may still be worth for some purposes (e.g. templating and translations).

Annotations should always be preferred to configuration files.

For development, a good webserver and a good database should not require any configuration. Embedded technologies can be used. We have chosen embedded GlassFish and embedded Derby.

We like EE technologies such as JTA, EJB, Injection, JAX-RS, so we cannot support Tomcat and Jetty. You can consider using TomEE. JetSet is not ready jet. We avoid Managed Beans, in favor of more general frameworks CDI and EJB. 

Technologies:
* java 8
* java EE 7, so in particular:
  * Servlet 3.1
  * JSTL 1.2 (needed?)
  * JAX-RS 2.0
  * JPA 2.1
  * JTA 1.2
  * CDI 1.0
  * JMS 2.0
  * JavaMail 1.5
* maven 3
* Embedded Glassfish as WAS
* Embedded Derby as database, configured as JTA datasource
* REST: JAX-RS
* MVC: jstl (needed?)
* persistence: JPA + hibernate 5 + annotations
* transactions: JTA + EJB
* connection pool: commons DBCP? C3PO?
* logging: sl4j + log4j
* Gentelella 1.4 frontend (jquery 3 + bootstrap 3)
* testing: junit 4
* javax.emails
* JAAS ? Java EE security? (up to Java EE 8 that's bad)
* session-based instead of token-based security, as we have both REST services and web pages

Suggested tools:
* Eclipse EE
* m2e plugin
* FileSync plugin

Features:
* JSP templating
* CRUD (via RESTful services)
* login page
* accounts management
* domain login
* authorizations
* all features as /modules/ so that they can be freely used or not
* translations: text-file-based JSTL translations. We think this is very ugly, anyway, it's quite standard.

Modules and interface should be independent: standalone Swing, or AngularJS frontends should work as well.

