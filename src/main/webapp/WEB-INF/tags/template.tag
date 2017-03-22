<%@tag description="WebApp Template"%>
<!DOCTYPE html>
<html>

<head>
<jsp:include page="/WEB-INF/tiles/head.jsp" />
</head>

<body>

	<!--  START HEADER -->
	<jsp:include page="/WEB-INF/tiles/pageHeader.jsp" />
	<!-- END HEADER -->

	<!--  START MENU -->
	<jsp:include page="/WEB-INF/tiles/pageMenu.jsp" />
	<!--  END MENU -->

	<!-- START BODY -->
	<div id="pageWrapper">

		<jsp:doBody />

	</div>

	<!--  START FOOTER -->
	<jsp:include page="/WEB-INF/tiles/pageFooter.jsp" />
	<!--  END FOOTER  -->

</body>
</html>