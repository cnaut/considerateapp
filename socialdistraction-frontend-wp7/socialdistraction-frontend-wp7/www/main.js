function getLocation() {
        navigator.geolocation.getCurrentPosition(onSuccess, onError);
		document.getElementById('checkin').style.display = 'none';
		document.getElementById('checkout').style.display = 'block';
}

// onSuccess Geolocation
//
function onSuccess(position) {
    var element = document.getElementById('geolocation');
    element.innerHTML = 'Latitude: '           + position.coords.latitude              + '<br />' +
                        'Longitude: '          + position.coords.longitude             + '<br />' +
                        'Altitude: '           + position.coords.altitude              + '<br />' +
                        'Accuracy: '           + position.coords.accuracy              + '<br />' +
                        'Altitude Accuracy: '  + position.coords.altitudeAccuracy      + '<br />' +
                        'Heading: '            + position.coords.heading               + '<br />' +
                        'Speed: '              + position.coords.speed                 + '<br />' +
                        'Timestamp: '          + new Date(position.timestamp)          + '<br />';

	var xmlhttp = new XMLHttpRequest();
	
	xmlhttp.onreadystatechange=function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			document.getElementById("response").innerHTML=xmlhttp.responseText;
		}
	}
	xmlhttp.open("POST","http://184.169.136.30:8001/battles/location",true);
	xmlhttp.send("lat=" + position.coords.latitude + "&long=" + position.coords.longitude);
}

// onError Callback receives a PositionError object
//
function onError(error) {
    alert('code: '    + error.code    + '\n' +
            'message: ' + error.message + '\n');
}

function checkin() {
	var options = {};
	PhoneGap.exec(null,null,"MobileCombat","checkin",options);
}

PhoneGap.addConstructor(function() {
    navigator.plugins.pgMobileCombat =
    {
        checkin:function()
        {
            var options = {};

            PhoneGap.exec(null,null,"MobileCombat","checkin",options);
        }
    }
});