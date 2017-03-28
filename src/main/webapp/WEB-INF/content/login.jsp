<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:templateNoMenu>
	<div class="col-md-12">
		<s:actionmessage />
		<s:actionerror />
	</div>

	<s:form action="login" method="post">

		<div class="row">
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<s:textfield key="userId" />
				<br />
				<s:password key="pwd" />
				<br />
				<s:submit value="login.submit" />
			</div>
		</div>

	</s:form>

</t:templateNoMenu>

