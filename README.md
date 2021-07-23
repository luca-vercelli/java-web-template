[![Build Status](https://travis-ci.org/luca-vercelli/java-web-template.svg?branch=master)](https://travis-ci.org/luca-vercelli/java-web-template)

# java-web-template
Template for real-world Java Web project.

Goal of this project: set up a new real-world Jave Web project in minutes, without waste of time.
We want pre-configure all possible technologies.
Moreover we want to use "best practice"'s wherever possible.

## How to use this project
This is not a framework. This is a webapp prototype. You can/must modify all classes and JSP's you need.

## How to run this project (with Eclipse)
* Download all included projects, either with Git or with eGit plugin
* Let Eclipse compile all (using m2e plugin)
* Run Java application *com.example.launcher.App*


## DISCLAIMER
Modern applications should be plain HTML, with data loaded asincronously via Ajax. RESt, OData, JSON technologies should be used.
So we consider Struts, Spring, JSF frameworks obsolete (tell me if and why not).
JSP/JSTL may still be worth for some purposes (e.g. templating and translations). Probably this is not true with Handlebars and/or Angular JS.

Annotations should always be preferred to configuration files.

For development, a good webserver and a good database should not require any configuration. Embedded technologies can be used. We have chosen embedded GlassFish and embedded Derby.

We like EE technologies such as JTA, EJB, Injection, JAX-RS, so we cannot support Tomcat and Jetty. You can consider using TomEE. JetSet is not ready jet. We avoid Managed Beans, in favor of more general frameworks CDI and EJB. 

### Technologies:
* Java 7
* Java EE 7, so in particular:
  * Servlet 3.1
  * JSTL 1.2 (needed?)
  * JAX-RS 2.0
  * JPA 2.1
  * JTA 1.2
  * CDI 1.0
  * JMS 2.0
  * JavaMail 1.5
  * WebSockets 1.0
* Maven 3
* Embedded Glassfish as WAS
* Embedded Derby as database, configured as JTA datasource
* REST: JAX-RS
* MVC: none. JSTL just for something. Really needed?
* persistence: JPA + hibernate 5 + annotations
* transactions: JTA + EJB
* logging: sl4j + log4j + custom CDI Producer
* Gentelella 1.4 frontend (jquery 3 + bootstrap 3). This could be replaced by tenths of other templates. Modified for use with JSP.
* CRUD module: "free datatables editor alternative" from https://github.com/KasperOlesen/DataTable-AltEditor
* Testing: junit 4. Jacoco?
* javax.emails + custom EJB
* Java EE security + jdbcRealm
* session-based instead of token-based security, as we have both REST services and web pages. Anyway we include an example of token-based authentication endpoint+filter.

### Suggested tools:
* Eclipse EE
* m2e plugin
* FileSync plugin

### Features:
* JSP templating
* CRUD (via RESTful services)
* login page
* accounts management
* domain login (TODO)
* authorizations (TODO)
* translations: text-file-based JSTL translations. We think this is very ugly, anyway, it's quite standard.
* first run install. We don't like JPA's sql-load-script-source feature, as it is not portable across databases.
* a custom I18nFilter, that in our opinion is easier to use than JSTL' fmt feature.

Modules and interface should be independent: standalone Swing, or AngularJS frontends should work as well.

### Talking about Security
We have tried several methods, and we still haven't find the "best practice".
* We don't like BASIC authentication, it's far from user's feelings. We want a custom login page.
* Java EE security's FORM authentication is better. The only problem is that no Filter is executed before login page, so
there can be issues e.g. with i18n. 
* Talking about authentication itself, the container provides login information, and this is quite ok,
the problem here is that every container has its own login methods, and they're not always good. For example, Glassfish' jdbcRealm is nice, however it supports a limited number of encryption algorithms.
* JASPIC is a solution, but it's not so easy, you need to modify the container itself. 
* We also tried to use an old JAAS LoginModule directly, however we do not recommend this.
