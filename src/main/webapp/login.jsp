<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="login.title"/></title>
</head>
<body>
<s:form action="Login" method="post">
<s:textfield key="userid"/><br/>
<s:textfield type="password" key="pwd"/><br/>
<s:submit value="login.submit"/>
</s:form>
</body>
</html>