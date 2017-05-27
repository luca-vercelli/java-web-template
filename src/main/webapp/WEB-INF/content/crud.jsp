<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template>

	<!-- FIXME what about CSS/JS page-dependent? -->
	<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.15/css/dataTables.bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.3.1/css/buttons.bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/select/1.2.2/css/select.bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="vendor/bootstrap/css/editor.bootstrap.min.css">


	<script type="text/javascript" src="//code.jquery.com/jquery-1.12.4.js">
	</script>
	<script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js">
	</script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/jquery.dataTables.min.js">
	</script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.15/js/dataTables.bootstrap.min.js">
	</script>
	<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js">
	</script>
	<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.3.1/js/buttons.bootstrap.min.js">
	</script>
	<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.2/js/dataTables.select.min.js">
	</script>
	<script type="text/javascript" src="vendor/dataTables/dataTables.editor.min.js">
	</script>
	<script type="text/javascript" src="vendor/dataTables/editor.bootstrap.min.js">
	</script>
	<script type="text/javascript" src="js/crud.js">
	</script>
	
	<!-- CONTENT HOME PAGE -->
	<!--  see https://editor.datatables.net/examples/styling/bootstrap.html -->
	
	<table id="crudTable" class="table table-striped table-bordered">
    </table>
    	
	<!-- END CONTENT -->
	
</t:template>