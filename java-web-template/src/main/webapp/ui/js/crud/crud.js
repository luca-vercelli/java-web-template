/**
 * WebTemplate 1.0 Luca Vercelli 2017 Released under MIT license
 */

// the global var 'entity' must have been set in main page

// Editor extension is not free. We must guess something.

var pageData = null;
var  WS_URL = "../../ws/rest/";



function PageData(entity, columns, tableSelector, modalDialogSelector) {

	this.entity = entity;
	this._columns = columns;
	this.tableSelector = tableSelector;
	this.modalDialogSelector = modalDialogSelector;
	this.datatable = null;

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
	 * Build a DataTable with 'ajax' option, single select, buttons, altEditor
	 */
	this.buildDataTable = function() {
		
		var myself = this;
		
	    this.datatable = $(this.tableSelector).DataTable( {
			// order: [[ 0, "desc" ]],
	        ajax: {
	            url: WS_URL + this.entity
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
		        },
				{
					text : 'XLS',
					action : function() {
						myself.exportXls();
					}
				}]
		});
	};

	this.exportXls = function() {
		window.open(WS_URL + entity + "/gridXLSX");
	};
}

function inspect(obj) {

	var s = "";
	for (var i in obj)
		s += i+":"+obj[i]+"\r\n";
	alert(s);

}
