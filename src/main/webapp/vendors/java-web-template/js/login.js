//
// WebTemplate 1.0
// Luca Vercelli 2017
// Released under GPLv3 
//
 $('.submit').click(function() {
	 alert("clicking");
	    $ajax({
	    		url: "rest/authentication",
	    		method: "GET",
	    		data: {
	    			userId: $('#Username'),
	    			pwd: $('#Password') //cleartext :( You should really use HTTPS.
	    		},
	    		success: function(data) {
	    			alert("DEBUG: " + data);
	    			//location.href = "";
	    		},
	    		error: function(data) {
	    			alert("Errors connecting login webservice. " + data);
	    		}
	    });
	    return false;
	});