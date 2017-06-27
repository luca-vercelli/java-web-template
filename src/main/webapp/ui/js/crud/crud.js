/**
 *  WebTemplate 1.0
 *  Luca Vercelli 2017
 *  Released under GPLv3 
 */

// the global var 'entity' must have been set in main page

// Editor extension is not free. We must guess something.

var pageData = null;

$(document).ready(function(){
	
	pageData = new PageData(entity, '#mainTable', '#modalDialog');
	pageData.askForGridAndDataThenBuildDataTable();
	
});

function PageData(entity, tableSelector, modalDialogSelector) {
	
	this.entity = entity;
	this.tableSelector = tableSelector;
	this.modalDialogSelector = modalDialogSelector;
	this.datatable = null;
	this._gridData = null;
	this._columns = null;
	this._form_fields = null;
	this.modalWindow = null;

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
			if (!gridData[x].readOnly)
			_form_fields.push({
				label: gridData[x].columnDefinition,
				name: gridData[x].columnDefinition //FIXME ...should decode...
			});
		}
		return _form_fields;
	};

	/**
	 * Create and return a modal window containing editable controls
	 */
	this.createModalWindow = function(form_fields) {
		
		$(this.modalDialogSelector).hide();
		
		var dt = null;

		//TODO
		
/*		dt = $(this.modalDialogSelector).DataTable({
			data: form_fields,
			columns: [
				{data: 'label' },
				{data: 'name' }
			]
		}); */
		
		return dt;
	};

	/**
	 * Ask server for data, then call buildDataTable()
	 */
	this.askForDataThenBuildDataTable = function(gridData) {
		
		this._gridData = gridData;
		this._columns = this.createColumns(gridData);
		this.buildDataTable();

		this._form_fields = this.createFormFields(gridData);
		this.modalWindow = this.createModalWindow(this._form_fields);

	};

	/**
	 * Build a DataTable with 'ajax' option, select, buttons
	 */
	this.buildDataTable = function() {
		
		//FIXME dataTable i18n?
		
		var myself = this;
		
	    this.datatable = $(this.tableSelector).DataTable( {
			// order: [[ 0, "desc" ]],
	        // TODO 
			ajax: {
				url: "../rest/" + this.entity,
				dataSrc: ""
			},
	        language: {
	        	url : 'language/datatables_it.json',
	        	dataType: 'json'
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
	 * Ask server for grid data, if needed, then call askForDataThenBuildDataTable()
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
				myself.askForDataThenBuildDataTable(data)
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
		$(this.modalDialogSelector).show();
	};

	/**
	* Crud implementation
	*/ 
	this.editRow = function() {
		alert("Not implemented yet!");
		$(this.modalDialogSelector).show();
	};

	/**
	* Crud implementation
	*/ 
	this.copyRow = function() {
		alert("Not implemented yet!");
		$(this.modalDialogSelector).show();
	};

	/**
	* Crud implementation
	*/ 
	this.deleteRow = function(x) {
		//delete of each one selected rows
		var myself = this;
		var rows = this.datatable.rows( { selected: true } );
		
		alert(rows.count());
		
		if (rows.count() == 0) {
			alert ('No rows selected.');
		} else {
			rows.every( function( rowIdx, tableLoop, rowLoop ) {
				var data = this.data();
				$.ajax({
				    url: "../rest/" + entity + "(" + data.id  +")",
				    type: 'DELETE',
				    success: myself.datatable.ajax.reload,
				    error: myself.serverError,
				    data: null,
				    contentType: 'json'
				  });
			});
		}
	};

	this.exportXls = function() {
		window.open("../rest/" + entity + "/gridXLSX");
	};
}

function inspect(obj) {

	var s = "";
	for (var i in obj)
		s += i+":"+obj[i]+"\r\n";
	alert(s);

}
