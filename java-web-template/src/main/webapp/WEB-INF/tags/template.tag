<%@ tag description="WebApp Template - Gentelella"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:set var="lang" scope="session">
   <c:out value="${cookie['JLANG'].value}" default="en_US"/>
</c:set>
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename="global" />

<html lang="${lang}">

<%@ attribute name="head_area" fragment="true"%>
<%@ attribute name="body_area" fragment="true" required="true"%>
<%@ attribute name="footer_area" fragment="true"%>

<head>
<t:head>
	<jsp:attribute name="head_area">
    		<jsp:invoke fragment="head_area" />
    	</jsp:attribute>
</t:head>
</head>

<body class="nav-md">

	<div class="container body">
		<div class="main_container">

			<!--  left side menu -->
			<t:menu />
			<!--  /left side menu -->

			<!-- top navigation -->
			<t:header />
			<!-- /top navigation -->

			<!-- page content -->
			<div class="right_col" role="main">
				<jsp:invoke fragment="body_area" />
			</div>
			<!-- /page content -->

			<!--  footer content -->
			<t:footer>
				<jsp:attribute name="footer_area">
    				<jsp:invoke fragment="footer_area" />
    			</jsp:attribute>
			</t:footer>
			<!--  /footer content  -->

		</div>
	</div>
	
	<script>
	<%-- some server-side variables may be here --%>
	var language = '${lang}';
	var userName = '${sessionBean.user.name}';
	</script>
	
	<script> //websockets
	
	var wsocketUrl = ((window.location.protocol == "https:") ? "wss:" : "ws:")
		+ "//" + window.location.host
		+ "/ws/wsocks";
	var wsocket = new WebSocket(wsocketUrl);
	
	</script>
</body>
</html>