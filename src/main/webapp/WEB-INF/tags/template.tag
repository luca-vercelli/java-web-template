<%@tag description="WebApp Template - Gentelella"%>
<html>

<head>
<jsp:include page="/WEB-INF/content/tiles/head.jsp" />
</head>

<body class="nav-md">

	<div class="container body">
		<div class="main_container">

			<!--  left side menu -->
			<jsp:include page="/WEB-INF/content/tiles/pageMenu.jsp" />
			<!--  /left side menu -->

			<!-- top navigation -->
			<jsp:include page="/WEB-INF/content/tiles/pageHeader.jsp" />
			<!-- /top navigation -->

			<!-- page content -->
			<div class="right_col" role="main">
				<jsp:doBody />
			</div>
			<!-- /page content -->

			<!--  footer content -->
			<jsp:include page="/WEB-INF/content/tiles/pageFooter.jsp" />
			<!--  /footer content  -->

		</div>
	</div>
</body>
</html>