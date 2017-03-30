# java-web-template
Template for real-world Java Web project

Tools:
* eclipse
* java 8
* maven 3
* MVC: struts 2 + conventions plugin (+annotations plugin?)
* persistence: JPA + hibernate 5 + annotations
* transactions: JTA? +annotations?
* connection pool: commons DBCP
* logging: commons logging + log4j
* UI: jquery 3 + bootstrap 3
* testing: junit 4
* SQLite as database (just for test)
* Apache Tomcat v8
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
