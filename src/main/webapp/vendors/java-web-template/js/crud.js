// WebTemplate 1.0
// Luca Vercelli 2017
// Released under GPLv3 

// the global var entity must have been set in main page
var gridData = null;

$(document).ready(function(){
	
	askForGridAndDataThenBuildDataTable();
	
});

function serverError(data, status) {

	$.bootstrapGrowl("Error while contacting server: " + data.status+ " - "+ data.statusText, {
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
		url: "../rest/" + entity + "/gridMetadata",
		type: "GET",
		dataType : "json",
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
	
	var _columns = [];
	for (var x in gridData) {
		_columns.push({
			'data': gridData[x].columnDefinition
		});
	}
	
	alert ("Here: " + _columns[0].data);
	alert ("and: " + _columns[1].data);
	
	//FIXME dataTable i18n?
	
	$("#mainTable").DataTable( {
		data: data,
		"autoWidth": false,
		// order: [[ 0, "desc" ]],
        language: {
        	decimal: ",",
            sEmptyTable:     "Nessun dato presente nella tabella",
            sInfo:           "Vista da _START_ a _END_ di _TOTAL_ elementi",
            sInfoEmpty:      "Vista da 0 a 0 di 0 elementi",
            sInfoFiltered:   "(filtrati da _MAX_ elementi totali)",
            sInfoPostFix:    "",
            sInfoThousands:  ".",
            sLengthMenu:     "Visualizza _MENU_ elementi",
            sLoadingRecords: "Caricamento...",
            sProcessing:     "Elaborazione...",
            sSearch:         "Cerca:",
            sZeroRecords:    "La ricerca non ha portato alcun risultato.",
            oPaginate: {
                sFirst:      "Inizio",
                sPrevious:   "Precedente",
                sNext:       "Successivo",
                sLast:       "Fine"
            },
            oAria: {
                sSortAscending:  ": attiva per ordinare la colonna in ordine crescente",
                sSortDescending: ": attiva per ordinare la colonna in ordine decrescente"
            }
        },
		columns: _columns
	});
}
