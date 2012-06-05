var battles;
var userID;
var accessToken;
var NUM_LOCATIONS = 5;
var users_timeout;


function joinBattle() {
    console.log("JOINBATTLE:" + this.rowIndex);
    var content
	= "fbid=" + userID + "&battle=" + battles.battles[this.rowIndex].id;
    console.log("JOINBATTLE:" + content);
    sendXmlhttpRequest("POST", content, "joinbattle", 
		       function() {
			   console.log("JOIN BATTLE SUCCESS");
			   window.location = 'battle.html';
		       }, 
		       function() {
			   console.log("JOIN BATTLE FAIL");
		       }, false, "");
}

/*
* Parse response from server and load battles into table
*/
function onBattlesRequestSuccess(serverResponse) {
    var table = document.getElementById('battles_table');
    while (table.rows.length > 0)
	table.deleteRow(0);
    battles = jQuery.parseJSON(serverResponse);
    for (var i = 0; i < battles.battles.length; i++) {
 	var row = table.insertRow(i);
	row.onclick = joinBattle;
 	
 	var cell = row.insertCell(0);
	cell.innerHTML = "" + battles.battles[i].name;
	var buttonCell = row.insertCell(1);
    }
}

/*
 * Function called when nearby.html is opened.
 */
function updateBattlesTable() {
    sendXmlhttpRequest("GET", null, "allbattles", onBattlesRequestSuccess, 
		       function() {
			   console.log("BATTLE REQUEST FAIL");
		       }, false, "");
}

function updateLocationsPopup(locations) {
    console.log("LOCATIONS RETURNED:" + locations.data.length);
    var popup = document.getElementById("popup");
    popup.innerHTML = "<form name='locationform'>";
    for (var i = 0; i < NUM_LOCATIONS; i++) {
	var place = locations.data[i].name;
	popup.innerHTML
	    += "<input type='radio' id='radio" + i
	    + "' value='" + place + "'>"
	    + place + "</input><br />";
    }
    popup.innerHTML
	+= "<button id='fight_button'>Let's fight!</button>"
	+ "<button id='cancel'>Cancel</button></form>";
    document.getElementById("fight_button").addEventListener("click", selectLocation, false);
    document.getElementById("cancel").addEventListener("click", closePopup, false);
    popup.className = "open";
}

function closePopup() {
    document.getElementById("popup").className = "closed";
}

function selectLocation() {
    var i;
    for (i = 0; i < NUM_LOCATIONS; i++) {
        if (document.getElementById("radio" + i).checked)
            break;
    }
    var content
	= "fbid=" + userID
	+ "&battlename=" + document.getElementById("radio" + i).value;
    console.log("SELECT LOCATION:" + content);
    sendXmlhttpRequest("POST", content, "createbattle", 
		       function() {
			   console.log("CREATE BATTLE SUCCESS");
			   closePopup();
			   window.location = 'battle.html';
		       },
		       function() {
			   console.log("CREATE BATTLE FAIL");
		       }, false, "");
}

/*
 * Create Battle
 */
function createBattle() {
    navigator.geolocation.getCurrentPosition(
	function(position) {
	    console.log("LOCATION SUCCESS");
	    var latlng = position.coords.latitude + "," + position.coords.longitude;
	    var dest = "/search?type=place&center=" + latlng + "&access_token=" + accessToken;
	    FB.api(dest, function (item) {
		if (item.error) {
		    console.log("FACEBOOK LOCATION FAIL");
		    console.log(JSON.stringify(item.error));
		} else {
		    console.log("FACEBOOK LOCATION SUCCESS");
		    console.log("item" + JSON.stringify(item));
		    updateLocationsPopup(item);
		}
	    });
	},
	function() {
	    console.log("LOCATION FAIL");
	}
    );
}

/*
 * Load battles table
 */
function onDeviceReady() {
    console.log("CHECKIN");
    accessToken = window.name;
    FB.api("/me?fields=id&access_token=" + accessToken, function (item) {
        if (item.error) {
	    console.log("FACEBOOK USER FAIL");
            console.log(JSON.stringify(item.error));
        } else {
	    console.log("FACEBOOK USER SUCCESS");
            userID = item.id;
	    console.log("GOT ID userID: " + userID);
	    document.getElementById("create_battle_button").addEventListener("click", createBattle, false);
	    poll(updateBattlesTable);
        }
    });
}

document.addEventListener("deviceready", onDeviceReady, false);

function poll(pollFunction) {
    pollFunction();
    clearTimeout(users_timeout);
    users_timeout = setTimeout(poll(pollFunction), 10000);
}