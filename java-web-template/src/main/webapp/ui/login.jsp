<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<c:set var="lang" scope="session">
   <c:out value="${cookie['JLANG'].value}" default="en_US"/>
</c:set>
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename="global" />

<fmt:message key="login.password.lost" var="login_password_lost" />


<html lang="${lang}">

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
    <link href="../build/css/custom.css" rel="stylesheet">
  </head>

  <body class="login">
    <div>
      <a class="hiddenanchor" id="signup"></a>
      <a class="hiddenanchor" id="signin"></a>
      <a class="hiddenanchor" id="rstpsw"></a>

      <div class="login_wrapper">
      
        <div class="animate form login_form" id="login_form">
          <section class="login_content">
            <form action="j_security_check" method="post">
            
              <h1>Login Form</h1>
        
              <div>
                <input type="text" class="form-control" placeholder="Username" required name="j_username" />
              </div>
              <div class="clearfix">
                    <c:if test="${sessionScope.error_message != null }">
						<fmt:message key="${sessionScope.error_message}" />
						<c:set var="error_message" scope="session" value="" />
					</c:if>
			  </div>
              <div>
                <input type="password" class="form-control" placeholder="Password" required name="j_password" />
              </div>
              <div>
                <input type="submit" class="btn btn-default submit" value="Log in"/> 
                <p class="change_link">
                  <a href="#rstpsw" class="to_register" id="btn_psw">${login_password_lost}</a>
                </p>
                <!-- <a class="reset_pass" href="#rstpsw">${login_password_lost}</a> -->
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
                	<option value="en" ${fn:substring(lang,0,2) == 'en' ? 'selected' : ''}>English</option>
                	<option value="it" ${fn:substring(lang,0,2) == 'it' ? 'selected' : ''}>Italiano</option>
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
        <div id="resetpsw" class="animate form resetpsw_form">
        <section class="login_content">
          	<!-- /password recovery -->
            <form action="doPasswordRecovery">
              <h1>Password Reset</h1>
              <div class="form-group has-feedback">
                <input type="email" class="form-control" name="email" placeholder="Your email" />
                <input type="hidden" name="address" id="address"/>
                <div class="form-control-feedback">
                  <i class="fa fa-envelope-o text-muted"></i>
                </div>
                <div>
                	<c:if test="${sessionScope.error_message != null }">
						<fmt:message key="${sessionScope.error_message}" />
						<c:set var="error_message" scope="session" value="" />
					</c:if>
                </div>
              </div>
              <button type="submit" class="btn bg-blue btn-block">Reset password <i class="fa fa-arrow-right position-right"></i></button>
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
            <!-- Password recovery -->
          </section>
		</div>
      </div>
    </div>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script>
    function changeLang() {
    	var lang = document.getElementById("languageSelect").value;
    	document.cookie = "JLANG=" + lang;
    	location.reload();
    }
    $(document).ready(function(){
	    $("#btn_psw").click(function(){
	    	$("#resetpsw").addClass("animazione_in");
	    	$("#login_form").addClass("animazione_out");
	    	$("#register").addClass("animazione_out");
	    });
    });
    </script>
  </body>
</html>
