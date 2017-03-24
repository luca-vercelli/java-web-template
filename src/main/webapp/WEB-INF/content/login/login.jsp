<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:templateNoMenu>
<s:actionerror/>
<s:form action="login" method="post">
<s:textfield key="userid"/><br/>
<s:password key="pwd"/><br/>
<s:submit value="login.submit"/>
</s:form>

</t:templateNoMenu>

