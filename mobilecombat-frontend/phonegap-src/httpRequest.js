/*
 * Handles communication to the server
 *
 */

var baseURL = "http://184.169.136.30:8002/";

/*
 * Get xmlhttprequest
 */
function getXmlhttpRequest() {
    var xmlhttp;
    try {
		xmlhttp = new XMLHttpRequest();
    } catch (trymicrosoft) {
		try {
			xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (othermicrosoft) {
			try {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (failed) {
				alert("http request could not be created");
			}
		}
    }
    return xmlhttp;
}

/* 
 * Create, format, and send an http request
 * mode: "GET" or "POST"
 * content: what you want to send
 * dest: "adduser" or "startbattle" or etc.
 * successFn: function to call on success (takes in a response from server)
 * failFn: function to call on fail (takes in a response from server)
 * multipart: (bool) whether we need a multipart request
 * boundary: boundary if we do have a multipart request
 */
function sendXmlhttpRequest(mode, content, dest, successFn, failFn) {

    // Create an xmlhttprequest
    var xmlhttp = getXmlhttpRequest();

    // Open port to URL
    xmlhttp.open(mode, baseURL + dest, true);

    // Declare the callback function for request
    xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4) {
			if(xmlhttp.status === 200) {
				successFn(xmlhttp.responseText);
				console.log("HTTPREQUEST " + dest + " server response: " + xmlhttp.responseText);
			} else {
				failFn(xmlhttp.responseText);
				console.log("HTTPREQUEST " + dest + " failed");
			} 
		}
    }

    // Send the request
    xmlhttp.send(content);
}