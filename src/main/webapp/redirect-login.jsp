<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Redirecting...</title>
</head>
<body>
<%-- 
 JS redirect, not meta redirect
 because we are not sure of base path
 FIXME should be more parametric
--%>
<script>
  var index = location.href.indexOf('/myapp'); 
  location.href = location.href.substring(0, index) + '/myapp/ui/login.jsp';
</script>
</body>
</html>