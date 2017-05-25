# java-web-template
Template for real-world Java Web project
Goal of this project: set up a new real-world Jave Web project in minutes, without waste of time.

CHANGELOG
We consider Struts and Spring frameworks obsolete.
Jstl is almost-obsolete, too.

We consider Jetty and Derby two good development-mode tools, because they can be embedded and (almost) no configuration is needed.

We like EE tools like JTA, EJB, JAX-RS, however we are not sure we can use them in Jetty.

Tools:
* eclipse
* java 8
* maven 3
* MVC: jstl (needed?)
* REST: JAX-RS?
* persistence: JPA + hibernate 5 + annotations
* transactions: JTA? +annotations?
* connection pool: commons DBCP? C3PO?
* logging: commons logging + log4j
* UI: jquery 3 + bootstrap 3
* testing: junit 4
* Derby as database
* Jetty
* datasource? (webserver-dependent)
* emails? (webserver-dependent)
* JAAS

Warning: Tomcat does not support JTA, nor sending emails. You need some libraries.

Features:
* JSP templating (and not struts tiles plugin)
* CRUD (via RESTful services)
* login page
* accounts management
* domain login
* authorizations
* all features as /modules/ so that they can be freely used or not

This template is intended for small/medium intranet web applications. No EJB's here.

Modules and interface should be independent: standalone Swing, or plain-HTML+AngularJS frontends should work as well.

See e.g. http://www.sharpknight.com/index.php/blog/50-struts2-best-practices
