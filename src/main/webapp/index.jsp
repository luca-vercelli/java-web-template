<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Redirecting...</title>
</head>
<body>
	<script>
		var dest = location.href;
		// IE 11 does not support Array.includes() nor Arrays.endsWith() :(
		// dest[dest.lenght-1] may give problems too
		if (dest.indexOf("?") >= 0) {
			dest = dest.substring(0, dest.indexOf("?"));
		}
		if (dest.length >= 12) {
			var idx1 = dest.indexOf("/welcome.jsp", dest.length - 12);
			if (idx1 >= 0) {
				dest = dest.substring(0, idx1);
			}
		}
		if (dest.substring(dest.length - 1) == "/") {
			dest = dest.substring(0, dest.length - 1);
		}
		dest += "/home";
		location.href = dest;
	</script>
</body>
</html>

