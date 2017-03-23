<%@tag description="WebApp Template"%>
<!DOCTYPE html>
<html>

<head>
<jsp:include page="/WEB-INF/jsp/tiles/head.jsp" />
</head>

<body>

	<jsp:include page="/WEB-INF/jsp/tiles/scripts.jsp" />
	
	<!--  START HEADER -->
	<jsp:include page="/WEB-INF/jsp/tiles/pageHeader.jsp" />
	<!-- END HEADER -->
	
	<!-- START BODY -->
	<div id="pageWrapper">

		<jsp:doBody />

	</div>

	<!--  START FOOTER -->
	<jsp:include page="/WEB-INF/jsp/tiles/pageFooter.jsp" />
	<!--  END FOOTER  -->

</body>
</html>