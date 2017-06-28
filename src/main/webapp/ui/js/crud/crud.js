/**
 * WebTemplate 1.0 Luca Vercelli 2017 Released under GPLv3
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
				title: gridData[x].columnDefinition // FIXME ...should decode...
			});
		}
		return _columns;
	};

	/**
	 * Ask server for data, then call buildDataTable()
	 */
	this.askForDataThenBuildDataTable = function(gridData) {
		
		this._gridData = gridData;
		this._columns = this.createColumns(gridData);
		this.buildDataTable();

	};

	/**
	 * Build a DataTable with 'ajax' option, single select, buttons, altEditor
	 */
	this.buildDataTable = function() {
		
		var myself = this;
		
	    this.datatable = $(this.tableSelector).DataTable( {
			// order: [[ 0, "desc" ]],
	        ajax: {
				url: "../rest/" + this.entity,
				dataSrc: ""
			},
			columns: this._columns,
	        language: {
	        	url : 'language/datatables_it.json', // FIXME i18n
	        	dataType: 'json'
	        },
	        select: true,
	        dom: 'Bfrtip', // buttons position
	        select: 'single',
	        responsive: true,
	        altEditor: true,     // Enable altEditor
	        buttons: [
	        	{
	            text: 'Add',
	            name: 'add'        // do not change name
	          },
	          {
	            extend: 'selected', // Bind to Selected row
	            text: 'Edit',
	            name: 'edit'        // do not change name
	          },
	          {
	            extend: 'selected', // Bind to Selected row
	            text: 'Delete',
	            name: 'delete'      // do not change name
	         }]
	        /*	        buttons: [
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
        ]*/
		});
	};

	/**
	 * Ask server for grid data, if needed, then call
	 * askForDataThenBuildDataTable()
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
				alert (myself.datatable.ajax); //DEBUG
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
		// delete of each one selected rows
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
