<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<c:set var="language" value="${fn:substring(sessionBean.language,0,2)}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="global" />
<fmt:message key="login.password.lost" var="login_password_lost" />


<html lang="${language}">

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><fmt:message key="application.title" /></title>

    <!-- Bootstrap -->
    <link href="../vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- Animate.css -->
    <link href="../vendors/animate.css/animate.min.css" rel="stylesheet">

    <!-- Custom Theme Style -->
    <link href="../build/css/custom.min.css" rel="stylesheet">
  </head>

  <body class="login">
    <div>
      <a class="hiddenanchor" id="signup"></a>
      <a class="hiddenanchor" id="signin"></a>

      <div class="login_wrapper">
        <div class="animate form login_form">
          <section class="login_content">
            <form action="doLogin" method="post">
            
            <input type="hidden" name="language" value="${language}"/>
            
              <h1>Login Form</h1>
        
              <div>
                <input type="text" class="form-control" placeholder="Username" required name="userId" />
              </div>
              <div>
                <input type="password" class="form-control" placeholder="Password" required name="pwd" />
              </div>
              <div>
                <input type="submit" class="btn btn-default submit" value="Log in"/> 
                <a class="reset_pass" href="login_password_recover.jsp">${labels.login_password_lost}</a>
              </div>

              <div class="clearfix"></div>

              <div class="separator">
                <p class="change_link">New to site?
                  <a href="#signup" class="to_register"> Create Account </a>
                </p>

                <div class="clearfix"></div>
                <br />

                <div>
                  <h1><i class="fa fa-paw"></i> Gentelella Alela!</h1>
                  <p>©2016 All Rights Reserved. Gentelella Alela! is a Bootstrap 3 template. Privacy and Terms</p>
                </div>
              </div>
            </form>
            
              <!-- i18n -->
              <form>
	            <select id="languageSelect" onchange="changeLang();" >
                	<option value="en" ${language == 'en' ? 'selected' : ''}>English</option>
                	<option value="it" ${language == 'it' ? 'selected' : ''}>Italiano</option>
            	</select>
        	  </form>
          </section>
        </div>

        <div id="register" class="animate form registration_form">
          <section class="login_content">
            <form>
              <h1>Create Account</h1>
              <div>
                <input type="text" class="form-control" placeholder="Username" required name="userId" />
              </div>
              <div>
                <input type="email" class="form-control" placeholder="Email" required name="email" />
              </div>
              <div>
                <input type="password" class="form-control" placeholder="Password" required name="pwd" />
              </div>
              <div>
                <a class="btn btn-default submit" href="doLogin">Submit</a>
              </div>

              <div class="clearfix"></div>

              <div class="separator">
                <p class="change_link">Already a member ?
                  <a href="#signin" class="to_register"> Log in </a>
                </p>

                <div class="clearfix"></div>
                <br />

                <div>
                  <h1><i class="fa fa-paw"></i> Gentelella Alela!</h1>
                  <p>©2016 All Rights Reserved. Gentelella Alela! is a Bootstrap 3 template. Privacy and Terms</p>
                </div>
              </div>
            </form>
          </section>
        </div>

      </div>
    </div>

	<script>
    function changeLang() {
    	var lang = document.getElementById("languageSelect").value;
    	document.cookie = "JLANG=" + lang;
    	location.reload();
    }
    
    </script>
  </body>
</html>
