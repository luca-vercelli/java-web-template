<%@tag description="WebApp Template"%>
<!DOCTYPE html>
<html>

<head>
<jsp:include page="/WEB-INF/content/tiles/head.jsp" />
</head>

<body>

	<jsp:include page="/WEB-INF/content/tiles/scripts.jsp" />
	
	<!--  START HEADER -->
	<jsp:include page="/WEB-INF/content/tiles/pageHeader.jsp" />
	<!-- END HEADER -->
	
	<!-- START BODY -->
	<div id="pageWrapper">

		<jsp:doBody />

	</div>

	<!--  START FOOTER -->
	<jsp:include page="/WEB-INF/content/tiles/pageFooter.jsp" />
	<!--  END FOOTER  -->

</body>
</html>