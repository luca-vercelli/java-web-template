// WebTemplate 1.0
// Luca Vercelli 2017
// Released under GPLv3 

// the global var entity must have been set in main page
var gridData = null;

$(document).ready(function(){
	
	askForGridAndDataThenBuildDataTable();
	
});

function serverError(data) {
	$.bootstrapGrowl("Error while contacting server: " + data, {
		type : 'danger',
		align : 'center',
		width : 'auto'
	});
}

/**
 * Ask server for grid data, if needed, then call askForData()
 */
function askForGridAndDataThenBuildDataTable() {

	if (gridData != null)
		return askForDataThenBuildDataTable(gridData);
	
	$.ajax({
		url: "../rest/Grid",
		type: "GET",
		dataType : "json",
		data : {
			entity: entity
		},
		success: askForDataThenBuildDataTable,
		error: serverError
	});

}

/**
 * Ask server for data, then call buildDataTable()
 */
function askForDataThenBuildDataTable(varGridData) {

	gridData = varGridData;
	
	$.ajax({
		url: "../rest/" + entity,
		type: "GET",
		dataType : "json",
		success: buildDataTable,
		error: serverError
	});

}

/**
 * Build datatable using data from 'data' and columns from 'gridData'
 */
function buildDataTable(data) {
	//TODO
	alert("Hello world");
}
