//
// WebTemplate 1.0
// Luca Vercelli 2017
// Released under GPLv3 
//
 $('#loginForm').submit(function() {
	    $ajax({
	    		url: "rest/authentication",
	    		method: "GET",
	    		data: {
	    			username: $('#Username'),
	    			password: $('#Password') //cleartext :(
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