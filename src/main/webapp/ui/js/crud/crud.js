/**
 *  WebTemplate 1.0
 *  Luca Vercelli 2017
 *  Released under GPLv3 
 */

// the global var 'entity' must have been set in main page

// Editor extension is not free. We must guess something.

var pageData = null;

$(document).ready(function(){
	
	pageData = new PageData(entity);
	pageData.askForGridAndDataThenBuildDataTable();
	
});

function PageData(entity) {
	
	this.entity = entity;
	this.editor = null
	this._gridData = null;
	this._columns = null;
	this._form_fields = null;

	/**
	 * Output an error message
	 */
	this.serverError = function(data, status) {

		$.bootstrapGrowl("Error while contacting server: " + data.status + " - " + data.statusText, {
			type : 'danger',
			align : 'center',
			width : 'auto'
		});
	};

	/**
	 * Create and return DataTable's columns definition array
	 */
	this.createColumns = function(gridData) {

		var _columns = [];
		for (var x in gridData) {
			_columns.push({
				data: gridData[x].columnDefinition,
				title: gridData[x].columnDefinition //FIXME ...should decode...
			});
		}
		return _columns;
	};

	/**
	 * Create and return Editor's form definition array
	 */
	this.createFormFields = function(gridData) {

		var _form_fields = [];
		for (var x in gridData) {
			_form_fields.push({
				label: gridData[x].columnDefinition,
				name: gridData[x].columnDefinition //FIXME ...should decode...
			});
		}
		return _form_fields;
	};

	/**
	 * Ask server for data, then call buildDataTable()
	 */
	this.askForDataThenBuildDataTable = function(gridData) {
		
		this._gridData = gridData;
		this._columns = this.createColumns(gridData);
		this._form_fields = this.createFormFields(gridData);

//		this.editor = new $.fn.dataTable.Editor({
//	        ajax: "../rest/" + this.entity,
//	        table: "#example",
//	        fields: this._form_fields
//	    } );
		
		this.buildDataTable();

	};

	/**
	 * Build a DataTable with 'ajax' option, select, buttons
	 */
	this.buildDataTable = function() {
		
		//FIXME dataTable i18n?
		
		var myself = this;
		
	    $("#mainTable").DataTable( {
			// order: [[ 0, "desc" ]],
	        // TODO 
			ajax: {
				url: "../rest/" + this.entity,
				dataSrc: ""
			},
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
	        select: true,
	        dom: 'Bfrtip',
	        buttons: [
				{
					text : 'New',
					className : 'button',
					action : function() {
						myself.newRow();
					}
				},
				{
					text : 'Edit',
					className : 'button',
					action : function(x) {
						myself.editRow(x);
					}
				},
				{
					text : 'Delete',
					className : 'button',
					action : function(x) {
						myself.deleteRow(x);
					}
				},
				{
					text : 'XLS',
					className : 'button',
					action : function() {
						myself.exportXls();
					}
				}
	        ],
			columns: this._columns
		});
	};

	/**
	 * Ask server for grid data, if needed, then call askForData()
	 */
	this.askForGridAndDataThenBuildDataTable = function() {
		
		if (this._gridData != null)
			return this.askForDataThenBuildDataTable(this._gridData);
		
		var myself = this;
		
		$.ajax({
			url: "../rest/" + entity + "/gridMetadata",
			type: "GET",
			dataType : "json",
			success: function(data) {
				myself.askForDataThenBuildDataTable(data);	
			},
			error: function(data) {
				myself.serverError(data);
			}
		});

	};

	/**
	* Crud implementation
	*/ 
	this.newRow = function() {
		alert("Not implemented yet!");
	}

	/**
	* Crud implementation
	*/ 
	this.editRow = function(x) {
		alert("Not implemented yet!");
	}

	/**
	* Crud implementation
	*/ 
	this.deleteRow = function(x) {
		alert("Not implemented yet!");
	}

	this.exportXls = function() {
		window.open("../rest/" + entity + "/gridXLSX");
	}
}

function inspect(obj) {

	var s = "";
	for (var i in obj)
		s += i+":"+obj[i]+"\r\n";
	alert(s);

}
