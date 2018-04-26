<!DOCTYPE html>
<!--
This file is not (yet) part of Gentelella. 
See PR #337
 -->
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

    <title>Gentellela Alela! | </title>

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

                  <a href="login.html#signup" class="to_register"> Create Account </a>
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
    <script>
    var address = location.href.substring(0, location.href.lastIndexOf("/"));
    document.getElementById("address").value = address;
    </script>
  </body>
</html>
