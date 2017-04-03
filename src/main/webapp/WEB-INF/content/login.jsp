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
				<s:label key="login.username" />
				<s:textfield name="userId" cssClass="form-control" />
			</div>
		</div>
		<div class="row">
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<s:label key="login.pwd" />
				<s:password name="pwd" cssClass="form-control" />
			</div>
		</div>
		<div class="row">
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<input type="submit" value="<s:text name="login.submit" />" class="form-control" />
			</div>
		</div>

	</s:form>

</t:templateNoMenu>

