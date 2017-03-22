<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login page</title>
</head>
<body>
<form action="Login" method="post">
<s:text name="login.username"/><input name="username"/><br/>
<s:text name="login.pwd"/><input type="password" name="pwd"/><br/>
<input type="submit" />
</form>
</body>
</html>